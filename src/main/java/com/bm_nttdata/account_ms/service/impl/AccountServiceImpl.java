package com.bm_nttdata.account_ms.service.impl;

import com.bm_nttdata.account_ms.client.CreditClient;
import com.bm_nttdata.account_ms.client.CustomerClient;
import com.bm_nttdata.account_ms.dto.CustomerDto;
import com.bm_nttdata.account_ms.entity.Account;
import com.bm_nttdata.account_ms.entity.BankFee;
import com.bm_nttdata.account_ms.entity.DailyBalance;
import com.bm_nttdata.account_ms.enums.AccountTypeEnum;
import com.bm_nttdata.account_ms.enums.FeeTypeEnum;
import com.bm_nttdata.account_ms.enums.OperationTypeEnum;
import com.bm_nttdata.account_ms.exception.AccountNotFoundException;
import com.bm_nttdata.account_ms.exception.ApiInvalidRequestException;
import com.bm_nttdata.account_ms.exception.BusinessRuleException;
import com.bm_nttdata.account_ms.exception.ServiceException;
import com.bm_nttdata.account_ms.mapper.AccountMapper;
import com.bm_nttdata.account_ms.model.AccountRequestDTO;
import com.bm_nttdata.account_ms.model.ApiResponseDTO;
import com.bm_nttdata.account_ms.model.DepositRequestDTO;
import com.bm_nttdata.account_ms.model.TransactionFeeRequestDTO;
import com.bm_nttdata.account_ms.model.TransactionFeeResponseDTO;
import com.bm_nttdata.account_ms.model.TransferRequestDTO;
import com.bm_nttdata.account_ms.model.WithdrawalRequestDTO;
import com.bm_nttdata.account_ms.repository.AccountRepository;
import com.bm_nttdata.account_ms.repository.BankFeeRepository;
import com.bm_nttdata.account_ms.repository.DailyBalanceRepository;
import com.bm_nttdata.account_ms.service.AccountService;
import com.bm_nttdata.account_ms.util.AccountNumberGenerator;
import com.bm_nttdata.account_ms.util.Constants;
import feign.FeignException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de los servicios de gestión de cuentas bancarias.
 * Proporciona la lógica de negocio para operaciones como creación, actualización,
 * consulta y eliminación de cuentas, así como operaciones de depósito, retiro y transferencia.
 * Esta clase maneja las reglas de negocio específicas para cada tipo de cuenta
 * y asegura la consistencia de las transacciones bancarias.
 */
@Slf4j
@Transactional
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BankFeeRepository bankFeeRepository;

    @Autowired
    private DailyBalanceRepository dailyBalanceRepository;

    private AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @Autowired
    private AccountNumberGenerator accountNumberGenerator;

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private CreditClient creditClient;

    /**
     * Retorna todas las cuentas bancarias de un cliente.
     *
     * @param customerId Identificador único del cliente
     * @param accountType Tipo de cuenta bancaria a filtrar
     * @return Lista de cuentas bancarias que coinciden con los criterios de búsqueda
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
     * Retorna una cuenta bancaria obtenida por el id.
     *
     * @param id Identificador único de la cuenta bancaria
     * @return Cuenta bancaria encontrada
     */
    @Override
    public Account getAccountById(String id) {
        return accountRepository.findById(id)
                .orElseThrow(
                        () -> new AccountNotFoundException("Account not found with id: " + id)
                );

    }

    /**
     * Crea una nueva cuenta bancaria en base a las reglas del negocio.
     *
     * @param accountRequest Objeto DTO con los datos necesarios para crear la cuenta
     * @return Nueva cuenta bancaria creada
     */
    @Override
    public Account createAccount(AccountRequestDTO accountRequest) {

        CustomerDto customer;

        try {
            customer = customerClient.getCustomerById(accountRequest.getCustomerId());

        } catch (FeignException e) {
            log.error("Error calling customer service: {}", e.getMessage());
            throw new ServiceException("Error retrieving customer information: " + e.getMessage());
        }

        validateAccountCreation(customer, accountRequest);
        Account account = initializeAccountByType(
                accountMapper.accountRequestDtoToEntityAccount(accountRequest));

        try {
            return accountRepository.save(account);
        } catch (Exception e) {
            log.error("Unexpected error while saving account: {}", e.getMessage());
            throw new ServiceException("Unexpected error creating account");
        }
    }

    /**
     * Actualiza datos globales de una cuenta bancaria.
     *
     * @param id Identificador único de la cuenta a actualizar
     * @param accountRequest Objeto DTO con los datos a actualizar
     * @return Cuenta bancaria actualizada
     */
    @Override
    public Account updateAccount(String id, AccountRequestDTO accountRequest) {

        Account account = getAccountById(id);
        try {
            accountMapper.updateEntityAccountFromAccountRequestDto(accountRequest, account);
            return accountRepository.save(account);
        } catch (Exception e) {
            log.error("Unexpected error while updating account {}: {}", id, e.getMessage());
            throw new ServiceException("Unexpected error updating account");
        }
    }

    /**
     * Elimina una cuenta bancaria que no tenga saldo.
     *
     * @param id Identificador único de la cuenta a eliminar
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
     * Retorna todas los saldos diarios de una cuenta bancaria.
     *
     * @param accountId Identificador único de la cuenta
     * @return Lista de saldos diarios que coinciden con los criterios de búsqueda
     */
    @Override
    public List<DailyBalance> getDailyBalances(String accountId, LocalDate searchMonth) {

        try {
            YearMonth month = YearMonth.from(searchMonth);
            LocalDate startDate = month.atDay(1);
            LocalDate endDate = month.atEndOfMonth();

            // Convertir LocalDate a ZonedDateTime en UTC
            ZonedDateTime startDateTime = startDate.atStartOfDay(ZoneOffset.UTC);
            ZonedDateTime endDateTime = endDate.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC);

            // Convertir a Date para la consulta
            Date startDateMongo = Date.from(startDateTime.toInstant());
            Date endDateMongo = Date.from(endDateTime.toInstant());

            List<DailyBalance> dailyBalanceList =
                    dailyBalanceRepository.findByAccountIdAndDateBetween(
                            accountId,
                            startDateMongo,
                            endDateMongo);
            log.info("Balances found: " + dailyBalanceList);
            return dailyBalanceList;
        } catch (Exception e) {
            log.error("Unexpected error while getting daily balances: {}", e.getMessage());
            throw new ServiceException(
                    "Unexpected error while getting daily balances" + e.getMessage());
        }
    }

    /**
     * Verifica Si la cuenta supera sus movimientos mensuales, para aplicar cargo de comision.
     *
     * @param transactionFeeRequest Objeto DTO con los datos necesarios para verificar la comisión
     * @return Objeto DTO con la respuesta sobre la aplicación de comisiones
     */
    @Override
    public TransactionFeeResponseDTO checkTransactionFee(
            TransactionFeeRequestDTO transactionFeeRequest) {

        Account account = getAccountById(transactionFeeRequest.getAccountId());
        TransactionFeeResponseDTO transactionFeeResponse = new TransactionFeeResponseDTO();

        transactionFeeResponse.setFeeAmount(BigDecimal.ZERO);
        transactionFeeResponse.setSubjectToFee(Boolean.FALSE);

        if ((transactionFeeRequest.getTransactionType()
                .equals(TransactionFeeRequestDTO.TransactionTypeEnum.DEPOSIT)
                || transactionFeeRequest.getTransactionType()
                .equals(TransactionFeeRequestDTO.TransactionTypeEnum.WITHDRAWAL))
                && (account.getCurrentMonthMovements() > account.getMonthlyMovementLimit())) {

            transactionFeeResponse.setFeeAmount(
                    BigDecimal.valueOf(Constants.AMOUNT_TRANSACTION_FEE));
            transactionFeeResponse.setSubjectToFee(Boolean.TRUE);
        }

        return transactionFeeResponse;
    }

    /**
     * Realiza un deposito verificando si se realiza el pago correspondiente de la
     * comision por superar limite de transacciones mensuales.
     *
     * @param depositRequest Objeto DTO con los datos necesarios para realizar el depósito
     * @return Objeto DTO con la respuesta del resultado de la operación
     */
    @Override
    public ApiResponseDTO makeDepositAccount(DepositRequestDTO depositRequest) {

        ApiResponseDTO apiResponse = new ApiResponseDTO();

        try {
            Account account = getAccountById(depositRequest.getTargetAccountId());
            String errorFound = validateTransaction(
                    account.getAccountType().getValue(), account.getWithdrawalDay());

            if (errorFound != "") {

                apiResponse.setStatus("FAILED");
                apiResponse.setMessage("Unprocessed deposit");
                apiResponse.setError(errorFound);
                return apiResponse;
            }

            TransactionFeeRequestDTO depositFeeRequest = new TransactionFeeRequestDTO();
            depositFeeRequest.setTransactionType(
                    TransactionFeeRequestDTO.TransactionTypeEnum.DEPOSIT);
            depositFeeRequest.setAccountId(depositRequest.getTargetAccountId());
            TransactionFeeResponseDTO transactionFeeResponse =
                    checkTransactionFee(depositFeeRequest);

            if (transactionFeeResponse.getSubjectToFee()) {

                if (!depositRequest.getFeeAmount()
                        .equals(transactionFeeResponse.getFeeAmount())) {
                    log.error("Incorrect transaction fee amount");
                    throw new BusinessRuleException("Incorrect transaction fee amount");
                }
                createBankFee(
                        account.getId(),
                        account.getAccountType(),
                        OperationTypeEnum.DEPOSIT,
                        FeeTypeEnum.TRANSACTION_EXCESS,
                        depositRequest.getFeeAmount());
            }

            account.setBalance(account.getBalance().add(depositRequest.getAmount()));
            account.setCurrentMonthMovements(account.getCurrentMonthMovements() + 1);
            account.setUpdatedAt(LocalDateTime.now());

            accountRepository.save(account);

            apiResponse.setStatus("SUCCESS");
            apiResponse.setMessage("Deposit successful");

            return apiResponse;

        } catch (Exception e) {

            apiResponse.setStatus("FAILED");
            apiResponse.setMessage("Unprocessed deposit");
            apiResponse.setError("Error when making the deposit: " + e.getMessage());

            return apiResponse;
        }
    }

    /**
     * Realiza un retiro verificando si se realiza el pago correspondiente de la comision
     * por superar limite de transacciones mensuales.
     *
     * @param withdrawalRequest Objeto DTO con los datos necesarios para realizar el retiro
     * @return Objeto DTO con la respuesta del resultado de la operación
     */
    @Override
    public ApiResponseDTO makeWithdrawalAccount(WithdrawalRequestDTO withdrawalRequest) {

        BigDecimal totalWithdrawal;
        ApiResponseDTO apiResponse = new ApiResponseDTO();

        try {
            Account account = getAccountById(withdrawalRequest.getSourceAccountId());
            String errorFound = validateTransaction(
                    account.getAccountType().getValue(), account.getWithdrawalDay());

            if (errorFound != "") {

                apiResponse.setStatus("FAILED");
                apiResponse.setMessage("Unprocessed withdrawal");
                apiResponse.setError(errorFound);
                return apiResponse;
            }

            TransactionFeeRequestDTO transactionFeeRequest = new TransactionFeeRequestDTO();
            transactionFeeRequest.setTransactionType(
                    TransactionFeeRequestDTO.TransactionTypeEnum.DEPOSIT);
            transactionFeeRequest.setAccountId(withdrawalRequest.getSourceAccountId());
            TransactionFeeResponseDTO transactionFeeResponse =
                    checkTransactionFee(transactionFeeRequest);

            totalWithdrawal = withdrawalRequest.getAmount();

            if (transactionFeeResponse.getSubjectToFee()) {

                if (!withdrawalRequest.getFeeAmount()
                        .equals(transactionFeeResponse.getFeeAmount())) {
                    log.error("Incorrect transaction fee amount");
                    throw new BusinessRuleException("Incorrect transaction fee amount");
                }
                createBankFee(
                        account.getId(),
                        account.getAccountType(),
                        OperationTypeEnum.WITHDRAWAL,
                        FeeTypeEnum.TRANSACTION_EXCESS,
                        withdrawalRequest.getFeeAmount());
            }

            if (withdrawalRequest.getAmount().compareTo(account.getBalance()) > 0) {
                apiResponse.setStatus("FAILED");
                apiResponse.setMessage("Withdrawal not processed");
                apiResponse.setError("Insufficient balance for retirement");

                return apiResponse;
            }

            account.setBalance(account.getBalance().subtract(totalWithdrawal));
            account.setCurrentMonthMovements(account.getCurrentMonthMovements() + 1);
            account.setUpdatedAt(LocalDateTime.now());

            accountRepository.save(account);

            apiResponse.setStatus("SUCCESS");
            apiResponse.setMessage("Withdrawal successful");

            return apiResponse;

        } catch (Exception e) {

            apiResponse.setStatus("FAILED");
            apiResponse.setMessage("Withdrawal not processed");
            apiResponse.setError("Withdrawal error: " + e.getMessage());

            return apiResponse;
        }
    }

    /**
     * Procesa una transferencia bancaria.
     * Valida la solicitud, verifica si la cuenta origen contiene saldo
     * disponible para realizar la transferancia.
     *
     * @param transferRequest DTO con los datos necesarios para realizar la transferencia
     * @return Objeto DTO con la respuesta del resultado de la operación
     */
    @Override
    public ApiResponseDTO processBankTransfer(TransferRequestDTO transferRequest) {

        ApiResponseDTO apiResponse = new ApiResponseDTO();

        try {
            Account sourceAccount = getAccountById(transferRequest.getSourceAccountId());
            Account targetAccount = getAccountById(transferRequest.getTargetAccountId());
            String errorFound = validateTransaction(
                    sourceAccount.getAccountType().getValue(), sourceAccount.getWithdrawalDay());

            if (!errorFound.equals("")) {

                apiResponse.setStatus("FAILED");
                apiResponse.setMessage("Unprocessed transfer");
                apiResponse.setError("Source Account: " + errorFound);
                return apiResponse;
            }

            errorFound = validateTransaction(
                    targetAccount.getAccountType().getValue(), targetAccount.getWithdrawalDay());

            if (!errorFound.equals("")) {

                apiResponse.setStatus("FAILED");
                apiResponse.setMessage("Unprocessed transfer");
                apiResponse.setError("Target Account: " + errorFound);
                return apiResponse;
            }

            if (transferRequest.getAmount().compareTo(sourceAccount.getBalance()) > 0) {
                apiResponse.setStatus("FAILED");
                apiResponse.setMessage("Transfer not processed");
                apiResponse.setError("Insufficient balance for transfer");

                return apiResponse;
            }

            sourceAccount.setBalance(
                    sourceAccount.getBalance().subtract(transferRequest.getAmount()));
            sourceAccount.setCurrentMonthMovements(sourceAccount.getCurrentMonthMovements() + 1);
            sourceAccount.setUpdatedAt(LocalDateTime.now());

            accountRepository.save(sourceAccount);

            targetAccount.setBalance(targetAccount.getBalance().add(transferRequest.getAmount()));
            targetAccount.setCurrentMonthMovements(targetAccount.getCurrentMonthMovements() + 1);
            targetAccount.setUpdatedAt(LocalDateTime.now());

            accountRepository.save(targetAccount);

            apiResponse.setStatus("SUCCESS");
            apiResponse.setMessage("Transfer successful");

            return apiResponse;

        } catch (Exception e) {

            apiResponse.setStatus("FAILED");
            apiResponse.setMessage("Transfer not processed");
            apiResponse.setError("Transfer error: " + e.getMessage());

            return apiResponse;
        }
    }

    /**
     * Retorna todas las cuentas bancarias segun un estatus indicado.
     *
     * @param status estatus de cuentas a buscar
     * @return Lista de cuentas bancarias que coinciden con los criterios de búsqueda
     */
    @Override
    public List<Account> getAccountsByStatus(String status) {
        List<Account> accountList = accountRepository.findByStatus(status);
        return accountList;
    }

    /**
     * Retorna todas las comisiones cobradas a una cuenta bancaria en un pediodo
     * de tiempo establecido.
     *
     * @param id Identificador único de la cuenta
     * @param startDate fecha inicial del período de consulta
     * @param endDate fecha final del período de consulta
     * @return Lista de saldos diarios que coinciden con los criterios de búsqueda
     */
    public List<BankFee> getBankFees(String id, LocalDate startDate, LocalDate endDate) {

        if (startDate.isAfter(endDate)) {
            log.error("Start date cannot be later than the search end date.");
            throw new ApiInvalidRequestException(
                    "Start date cannot be later than the search end date: ");
        }

        try {
            ZonedDateTime startDateTime = startDate.atStartOfDay(ZoneOffset.UTC);
            ZonedDateTime endDateTime = endDate.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC);

            Date startDateMongo = Date.from(startDateTime.toInstant());
            Date endDateMongo = Date.from(endDateTime.toInstant());

            List<BankFee> bankFeeList =
                    bankFeeRepository.findByAccountIdAndDateBetween(
                            id,
                            startDateMongo,
                            endDateMongo);
            log.info("Bank Fees found: " + bankFeeList);
            return bankFeeList;
        } catch (Exception e) {
            log.error("Unexpected error while getting bank fees: {}", e.getMessage());
            throw new ServiceException(
                    "Unexpected error while getting bank fees" + e.getMessage());
        }
    }

    /**
     * Valida las reglas del negocio para creacion de cuenta bancaria en base al tipo de cliente.
     *
     * @param customer cliente a validar
     * @param accountRequest solicitud de cuenta a crear
     */
    private void validateAccountCreation(CustomerDto customer, AccountRequestDTO accountRequest) {

        String customerType = customer.getCustomerType();

        switch (customerType) {

            case "PERSONAL" -> {
                long count = accountRepository.countByCustomerIdAndAccountType(
                        customer.getId(), accountRequest.getAccountType().toString());
                if (count > 0) {
                    throw new BusinessRuleException("Customer already has a "
                        + accountRequest.getAccountType().toString() + " account");
                }

                if (accountRequest.getAccountHolders().size() > 0
                        || accountRequest.getAuthorizedSigners().size() > 0) {
                    throw new BusinessRuleException(
                        "A personal account shouldn't have account holder or authorized signers");
                }
            }
            case "BUSINESS" -> {
                if ("SAVINGS".equals(accountRequest.getAccountType().getValue())
                        || "FIXED_TERM".equals(accountRequest.getAccountType().getValue())) {
                    throw new BusinessRuleException(
                        "Business customer can't have SAVINGS or FIXED_TERM accounts");
                }

                if (accountRequest.getAccountHolders().size() == 0) {
                    throw new BusinessRuleException(
                        "A business account must have at least one account holder");
                }
            }
            default -> throw new BusinessRuleException("Customer type not supported: "
                    + customerType);
        }

        String accountType = accountRequest.getAccountType().getValue();

        List<String> restrictedAccountTypes = List.of(
                AccountTypeEnum.SAVINGS_VIP.getValue(),
                AccountTypeEnum.CHECKING_PYME.getValue()
        );

        boolean requiresCreditCard = restrictedAccountTypes.contains(accountType)
                && creditClient.getAllCreditCards(customer.getId()).isEmpty();

        if (requiresCreditCard) {
            throw new BusinessRuleException(
                    "Customer must have a credit card to apply for this product"
            );
        }

        boolean requiresMinimumBalance = restrictedAccountTypes.contains(accountType)
                && accountRequest.getInitialBalance()
                .compareTo(Constants.VIP_MINIMUM_OPENING_BALANCE) < 0;

        if (requiresMinimumBalance) {
            throw new BusinessRuleException(
                    "Opening balance less than the required amount of "
                    + Constants.VIP_MINIMUM_OPENING_BALANCE
            );
        }
    }

    /**
     * Establece las atributos necesarios segun el tipo de cuenta bancaria a crear.
     *
     * @param account la cuenta a configurar
     * @return la cuenta con atributos necesarios segun el tipo de cuenta
     */
    private Account initializeAccountByType(Account account) {

        switch (account.getAccountType().getValue()) {
            case "SAVINGS" -> account = setupSavingsAccount(account);
            case "CHECKING" -> account = setupCheckingAccount(account);
            case "FIXED_TERM" -> account = setupFixedTermAccount(account);
            case "SAVINGS_VIP" -> account = setupSavingsVipAccount(account);
            case "CHECKING_MYPE" -> account = setupCheckingMypeAccount(account);
            default ->
                throw new BusinessRuleException("Tipo de cuenta no soportado: "
                        + account.getAccountType().getValue());
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
     * Establece los atributos especificos para una cuenta de ahorro.
     *
     * @param account la cuenta a configurar
     * @return la cuenta con atributos especificos para una de ahorro
     */
    private Account setupSavingsAccount(Account account) {
        account.setMaintenanceFee(0.0);
        account.setMonthlyMovementLimit(8);
        //account.setBalance(BigDecimal.ZERO);
        account.setWithdrawalDay(0);
        return account;
    }

    /**
     * Establece los atributos especificos para una cuenta corriente.
     *
     * @param account la cuenta a configurar
     * @return la cuenta con atributos especificos para una cuenta corriente
     */
    private Account setupCheckingAccount(Account account) {
        if (account.getMaintenanceFee() == null) {
            account.setMaintenanceFee(10.0);
        }
        account.setMonthlyMovementLimit(8);
        //account.setBalance(BigDecimal.ZERO);
        account.setWithdrawalDay(0);
        return account;
    }

    /**
     * Establece los atributos especificos para una cuenta a plazo fijo.
     *
     * @param account la cuenta a configurar
     * @return la cuenta con atributos especificos para una cuenta a plazo fijo
     */
    private Account setupFixedTermAccount(Account account) {
        account.setMaintenanceFee(0.0);
        account.setMonthlyMovementLimit(8);
        //account.setBalance(BigDecimal.ZERO);
        if (account.getWithdrawalDay() == null) {
            account.setWithdrawalDay(5);
        }
        return account;
    }

    /**
     * Establece los atributos especificos para una cuenta de ahorro VIP.
     *
     * @param account la cuenta a configurar
     * @return la cuenta con atributos especificos para una de ahorro VIP
     */
    private Account setupSavingsVipAccount(Account account) {
        account.setMaintenanceFee(0.0);
        account.setMonthlyMovementLimit(8);
        //account.setBalance(BigDecimal.ZERO);
        account.setWithdrawalDay(0);
        return account;
    }

    /**
     * Establece los atributos especificos para una cuenta corriente PYME.
     *
     * @param account la cuenta a configurar
     * @return la cuenta con atributos especificos para una cuenta corriente PYME
     */
    private Account setupCheckingMypeAccount(Account account) {
        if (account.getMaintenanceFee() == null) {
            account.setMaintenanceFee(0.0);
        }
        account.setMonthlyMovementLimit(8);
        //account.setBalance(BigDecimal.ZERO);
        account.setWithdrawalDay(0);
        return account;
    }

    /**
     * Valida que la cuenta bancaria no tenga saldo para la eliminacion.
     *
     * @param account la cuenta a cvalidar
     */
    private void validateAccountDeletion(Account account) {
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessRuleException("An account with a balance can't be deleted.");
        }
    }

    private String validateTransaction(String typeAccount, int allowedTransactionDay) {

        String errorMessage = "";

        if ((typeAccount.equals("FIXED_TERM"))
                && (LocalDate.now().getDayOfMonth() != allowedTransactionDay)) {
            errorMessage = "Fixed-term account: Day not allowed to make transactions";
        }

        return errorMessage;
    }

    /**
     * Registra una comision bancaria.
     *
     * @param accountId identificador de cuenta bancaria
     * @param accountType descripcion de tipo de cuenta bancaria
     * @param operationType descripcion de tipo de operacion por la que se registra la comision
     * @param feeType tipo de comision
     * @param feeAmount monto de comision
     */
    private void createBankFee(
            String accountId, AccountTypeEnum accountType, OperationTypeEnum operationType,
            FeeTypeEnum feeType, BigDecimal feeAmount) {

        try {

            BankFee bankFee = BankFee.builder()
                    .accountId(accountId)
                    .accountType(accountType)
                    .operationType(operationType)
                    .date(LocalDate.now())
                    .feeType(feeType)
                    .feeAmount(feeAmount)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            bankFeeRepository.save(bankFee);
        } catch (Exception e) {
            log.error("Unexpected error while saving bankFee: {}", e.getMessage());
            throw new ServiceException("Unexpected error creating bankFee" + e.getMessage());
        }
    }
}
