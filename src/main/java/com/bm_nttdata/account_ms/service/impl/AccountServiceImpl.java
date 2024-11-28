package com.bm_nttdata.account_ms.service.impl;

import com.bm_nttdata.account_ms.DTO.OperationResponseDTO;
import com.bm_nttdata.account_ms.entity.Account;
import com.bm_nttdata.account_ms.exception.*;
import com.bm_nttdata.account_ms.mapper.AccountMapper;
import com.bm_nttdata.account_ms.model.*;
//import com.bm_nttdata.account_ms.model.FeeResponseDTO;
import com.bm_nttdata.account_ms.repository.AccountRepository;
import com.bm_nttdata.account_ms.util.AccountNumberGenerator;
import com.bm_nttdata.account_ms.DTO.CustomerDTO;
import com.bm_nttdata.account_ms.client.CustomerClient;
import com.bm_nttdata.account_ms.entity.Account;
import com.bm_nttdata.account_ms.exception.AccountNotFoundException;
import com.bm_nttdata.account_ms.exception.BusinessRuleException;
import com.bm_nttdata.account_ms.mapper.AccountMapper;
import com.bm_nttdata.account_ms.repository.AccountRepository;
import com.bm_nttdata.account_ms.service.IAccountService;
import com.bm_nttdata.account_ms.util.Constants;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Slf4j
@Transactional
@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private AccountRepository accountRepository;

    private AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @Autowired
    private AccountNumberGenerator accountNumberGenerator;
    @Autowired
    private CustomerClient customerClient;

    /**
     *  Retorna todas las cuentas bancarias de un cliente
     * @param customerId
     * @param accountType
     * @return
     */
    @Override
    public List<Account> getAllAccounts(String customerId, String accountType) {

        if (customerId == null) {
            throw new ApiInvalidRequestException("CustomerId is required");
        }

        List<Account> accountList = Stream.of(accountType)
                .filter(type -> type != null && !type.trim().isEmpty())
                .findFirst()
                .map(type -> accountRepository.findByCustomerIdAndAccountType(customerId, type))
                .orElseGet(() -> accountRepository.findByCustomerId(customerId));

        return accountList;
    }

    /**
     * Retorna una cuenta bancaria obtenida por el id
     * @param id
     * @return
     */
    @Override
    public Account getAccountById(String id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));

    }

    /**
     * Crea una nueva cuenta bancaria en base a las reglas del negocio
     * @param accountRequest
     * @return
     */
    @Override
    public Account createAccount(AccountRequestDTO accountRequest) {

        CustomerDTO customerDTO = new CustomerDTO();

        try{
            customerDTO = customerClient.getCustomerById(accountRequest.getCustomerId());

        } catch (FeignException e){
            log.error("Error calling customer service: {}", e.getMessage());
            throw new ServiceException("Error retrieving customer information: " + e.getMessage());
        }

        validateAccountCreation(customerDTO, accountRequest);
        Account account = initializeAccountByType(accountMapper.accountRequestDtoToEntityAccount(accountRequest));

        try{
            return accountRepository.save(account);
        }catch (Exception e) {
            log.error("Unexpected error while saving account: {}", e.getMessage());
            throw new ServiceException("Unexpected error creating account");
        }
    }

    /**
     * Actualiza datos globales de una cuenta bancaria
     * @param id
     * @param request
     * @return
     */
    @Override
    public Account updateAccount(String id, AccountRequestDTO request) {

        Account account = getAccountById(id);
        try{
            accountMapper.updateEntityAccountFromAccountRequestDto(request, account);
            return accountRepository.save(account);
        }catch (Exception e){
            log.error("Unexpected error while updating account {}: {}", id, e.getMessage());
            throw new ServiceException("Unexpected error updating account");
        }
    }

    /**
     * Elimina una cuenta bancaria que no tenga saldo
     * @param id
     */
    @Override
    public void deleteAccount(String id) {

        Account account = getAccountById(id);
        validateAccountDeletion(account);

        try {
            accountRepository.delete(account);
        } catch (Exception e) {
            log.error("Error deleting account {}: {}", id, e.getMessage());
            throw new ServiceException("Error deleting account: " + e.getMessage());
        }

    }

    /**
     * Verifica Si la cuenta supera sus movimientos mensuales, para aplicar cargo de comision
     * @param id
     * @param transactionFeeRequestDTO
     * @return
     */
    @Override
    public TransactionFeeResponseDTO checkTransactionFee(String id, TransactionFeeRequestDTO transactionFeeRequestDTO) {

        Account account = getAccountById(id);
        TransactionFeeResponseDTO transactionFeeResponseDTO = new TransactionFeeResponseDTO();

        transactionFeeResponseDTO.setFeeAmount(BigDecimal.ZERO);
        transactionFeeResponseDTO.setSubjectToFee(Boolean.FALSE);

        if ((transactionFeeRequestDTO.getTransactionType().equals(TransactionFeeRequestDTO.TransactionTypeEnum.DEPOSIT) ||
            transactionFeeRequestDTO.getTransactionType().equals(TransactionFeeRequestDTO.TransactionTypeEnum.WITHDRAWAL)) &&
                (account.getMonthlyMovementLimit() > account.getCurrentMonthMovements())){

            transactionFeeResponseDTO.setFeeAmount(BigDecimal.valueOf(Constants.AMOUNT_TRANSACTION_FEE));
            transactionFeeResponseDTO.setSubjectToFee(Boolean.TRUE);
        }

        return transactionFeeResponseDTO;
    }

    /**
     * Realiza un deposito verificando si se realiza el pago correspondiente de la comision por superar limite de
     * transacciones mensuales
     * @param id
     * @param transactionRequestDTO
     * @return
     */
    @Override
    public ApiResponseDTO makeDepositAccount(String id, TransactionRequestDTO transactionRequestDTO) {

        BigDecimal totalDeposit;
        ApiResponseDTO apiResponseDTO = new ApiResponseDTO();

        try {
            Account account = getAccountById(id);

            TransactionFeeRequestDTO transactionFeeRequestDTO = new TransactionFeeRequestDTO();
            transactionFeeRequestDTO.setTransactionType(TransactionFeeRequestDTO.TransactionTypeEnum.DEPOSIT);
            transactionFeeRequestDTO.setTransactionMade(transactionRequestDTO.getTransactionMade());
            TransactionFeeResponseDTO transactionFeeResponseDTO = checkTransactionFee(id, transactionFeeRequestDTO);

            totalDeposit = transactionRequestDTO.getTransactionAmount();

            if (transactionFeeResponseDTO.getSubjectToFee()){

                if (!transactionRequestDTO.getFeeAmount().equals(transactionFeeResponseDTO.getFeeAmount())){
                    log.error("Incorrect transaction fee amount");
                    throw new BusinessRuleException("Incorrect transaction fee amount");
                }
            }

            account.setBalance(account.getBalance().add(totalDeposit));
            account.setCurrentMonthMovements(account.getCurrentMonthMovements() + 1);

            accountRepository.save(account);

            apiResponseDTO.setStatus("SUCCESS");
            apiResponseDTO.setMessage("Deposit successful");

            return apiResponseDTO;

        } catch (Exception e){

            apiResponseDTO.setStatus("FAILED");
            apiResponseDTO.setMessage("Unprocessed deposit");
            apiResponseDTO.setError("Error when making the deposit: " + e.getMessage());

            return apiResponseDTO;
        }
    }

    /**
     * Realiza un retiro verificando si se realiza el pago correspondiente de la comision por superar limite de
     * transacciones mensuales
     * @param id
     * @param transactionRequestDTO
     * @return
     */
    @Override
    public ApiResponseDTO makeWithdrawalAccount(String id, TransactionRequestDTO transactionRequestDTO) {

        BigDecimal totalWithdrawal;
        ApiResponseDTO apiResponseDTO = new ApiResponseDTO();

        try {
            Account account = getAccountById(id);

            TransactionFeeRequestDTO transactionFeeRequestDTO = new TransactionFeeRequestDTO();
            transactionFeeRequestDTO.setTransactionType(TransactionFeeRequestDTO.TransactionTypeEnum.DEPOSIT);
            transactionFeeRequestDTO.setTransactionMade(transactionRequestDTO.getTransactionMade());
            TransactionFeeResponseDTO transactionFeeResponseDTO = checkTransactionFee(id, transactionFeeRequestDTO);

            totalWithdrawal = transactionRequestDTO.getTransactionAmount();

            if (transactionFeeResponseDTO.getSubjectToFee()){

                if (!transactionRequestDTO.getFeeAmount().equals(transactionFeeResponseDTO.getFeeAmount())){
                    log.error("Incorrect transaction fee amount");
                    throw new BusinessRuleException("Incorrect transaction fee amount");
                }
            }

            account.setBalance(account.getBalance().subtract(totalWithdrawal));
            account.setCurrentMonthMovements(account.getCurrentMonthMovements() + 1);

            accountRepository.save(account);

            apiResponseDTO.setStatus("SUCCESS");
            apiResponseDTO.setMessage("Withdrawal successful");

            return apiResponseDTO;

        } catch (Exception e){

            apiResponseDTO.setStatus("FAILED");
            apiResponseDTO.setMessage("Withdrawal not processed");
            apiResponseDTO.setError("Withdrawal error: " + e.getMessage());

            return apiResponseDTO;
        }
    }

    /**
     * Valida las reglas del negocio para creacion de cuenta bancaria en base al tipo de cliente
     * @param customerDTO
     * @param accountRequestDTO
     */
    private void validateAccountCreation(CustomerDTO customerDTO, AccountRequestDTO accountRequestDTO) {

        if (customerDTO.getCustomerType().equals("PERSONAL")){
            long count = accountRepository.countByCustomerIdAndAccountType(customerDTO.getId(), accountRequestDTO.getAccountType().toString());
            if (count > 0) {
                throw new BusinessRuleException("Customer already has a " +
                        accountRequestDTO.getAccountType().toString() + " account");
            }

            if (accountRequestDTO.getAccountHolders().size() > 0 || accountRequestDTO.getAuthorizedSigners().size() > 0 ){
                throw new BusinessRuleException("A personal account shouldn't have account holder or aothorized signers");
            }

        }
        else {
            if ("SAVINGS".equals(accountRequestDTO.getAccountType().getValue()) || "FIXED_TERM".equals(accountRequestDTO.getAccountType().getValue())) {
                throw new BusinessRuleException("Business customer can't have SAVINGS or FIXED_TERM accounts");
            }

            if (accountRequestDTO.getAccountHolders().size() == 0){
                throw new BusinessRuleException("A business account must have at least one account holder");
            }
        }
    }

    /**
     * Establece las atributos necesarios segun el tipo de cuenta bancaria a crear
     * @param account
     * @return
     */
    private Account initializeAccountByType(Account account){

        switch (account.getAccountType().getValue()) {
            case "SAVINGS":
                account = setupSavingsAccount(account);
                break;
            case "CHECKING":
                account = setupCheckingAccount(account);
                break;
            case "FIXED_TERM":
                account = setupFixedTermAccount(account);
                break;
            default:
                throw new BusinessRuleException("Tipo de cuenta no soportado: " + account.getAccountType().getValue());
        }

        // Valores comunes para todos los tipos de cuenta
        try {
            account.setAccountNumber(accountNumberGenerator.generateAccountNumber());
        } catch (Exception e) {
            log.error("Error generating account number: {}", e.getMessage());
            throw new ServiceException("Error generating account number");
        }
        account.setStatus("ACTIVE");
        account.setCurrentMonthMovements(0);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        return account;
    }

    /**
     * Establece los atributos especificos para una cuenta de ahorro
     * @param account
     * @return
     */
    private Account setupSavingsAccount(Account account) {
        account.setMaintenanceFee(0.0);
        account.setMonthlyMovementLimit(8);
        account.setBalance(BigDecimal.ZERO);
        account.setWithdrawalDay(0);
        return account;
    }

    /**
     * Establece los atributos especificos para una cuenta corriente
     * @param account
     * @return
     */
    private Account setupCheckingAccount(Account account) {
        if (account.getMaintenanceFee() == null) account.setMaintenanceFee(10.0);
        account.setMonthlyMovementLimit(8);
        account.setBalance(BigDecimal.ZERO);
        account.setWithdrawalDay(0);
        return account;
    }

    /**
     * Establece los atributos especificos para una cuenta a plazo fijo
     * @param account
     * @return
     */
    private Account setupFixedTermAccount(Account account) {
        account.setMaintenanceFee(0.0);
        account.setMonthlyMovementLimit(8);
        account.setBalance(BigDecimal.ZERO);
        if (account.getWithdrawalDay() == null)  account.setWithdrawalDay(5);
        return account;
    }

    /**
     * Valida que la cuenta bancaria no tenga saldo para la eliminacion
     * @param account
     */
    private void validateAccountDeletion(Account account){
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessRuleException("An account with a balance can't be deleted.");
        }
    }

}
