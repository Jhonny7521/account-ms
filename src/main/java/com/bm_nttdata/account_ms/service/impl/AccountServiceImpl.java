package com.bm_nttdata.account_ms.service.impl;
/*
import com.bm_nttdata.account_ms.entity.Account;
import com.bm_nttdata.account_ms.exception.AccountNotFoundException;
import com.bm_nttdata.account_ms.exception.BusinessRuleException;
import com.bm_nttdata.account_ms.mapper.AccountMapper;
import com.bm_nttdata.account_ms.model.AccountRequest;
import com.bm_nttdata.account_ms.repository.AccountRepository;
import com.bm_nttdata.account_ms.util.AccountNumberGenerator;*/
import com.bm_nttdata.account_ms.DTO.CustomerDTO;
import com.bm_nttdata.account_ms.client.CustomerClient;
import com.bm_nttdata.account_ms.entity.Account;
import com.bm_nttdata.account_ms.exception.AccountNotFoundException;
import com.bm_nttdata.account_ms.exception.BusinessRuleException;
import com.bm_nttdata.account_ms.mapper.AccountMapper;
import com.bm_nttdata.account_ms.model.AccountDTO;
import com.bm_nttdata.account_ms.model.SavingsAccountDTO;
import com.bm_nttdata.account_ms.repository.AccountRepository;
import com.bm_nttdata.account_ms.service.IAccountService;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Slf4j
@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private AccountRepository accountRepository;

    private AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @Autowired
    private CustomerClient customerClient;

    @Override
    public List<Account> getAllAccounts(String customerId, String accountType) {

        if (customerId == null) {
            throw new BusinessRuleException("CustomerId is required");
        }

        List<Account> accountList = Stream.of(accountType)
                .filter(type -> type != null && !type.trim().isEmpty())
                .findFirst()
                .map(type -> accountRepository.findByCustomerIdAndAccountType(customerId, type))
                .orElseGet(() -> accountRepository.findByCustomerId(customerId));

        return accountList;
    }

    @Override
    public Account getAccountById(String id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: \" + id"));

    }

    @Override
    public Account createAccount(AccountDTO accountRequest) {

        CustomerDTO customerDTO = customerClient.getCustomerById(accountRequest.getCustomerId());

        validateAccountCreation(customerDTO, accountRequest);

        return null;
    }

    @Override
    public Account updateAccount(String id, Account request) {
        return null;
    }

    @Override
    public void deleteAccount(String id) {

    }

    private void validateAccountCreation(CustomerDTO customerDTO, AccountDTO accountDTO) {

        if (customerDTO.getCustomerType().equals("PERSONAL")){
            long count = accountRepository.countByCustomerIdAndAccountType(customerDTO.getId(), accountDTO.getAccountType().toString());
            if (count > 0) {
                throw new BusinessRuleException("Customer already has a " +
                        accountDTO.getAccountType().toString() + " account");
            }

            if (accountDTO.getAccountHolders().size() > 1 || accountDTO.getAuthorizedSigners().size() > 1 ){
                throw new BusinessRuleException("A personal account shouldn't have account holder or aothorized signers");
            }

        }
        else {
            if ("SAVINGS".equals(accountDTO.getAccountType()) || "FIXED_TERM".equals(accountDTO.getAccountType())) {
                throw new BusinessRuleException("Business customer can't have SAVINGS or FIXED_TERM accounts");
            }

            if (accountDTO.getAccountHolders().size() == 0){
                throw new BusinessRuleException("A business account must have at least one account holder");
            }

        }

        if (accountDTO.getAccountType().equals("SAVINGS")){
            if (accountDTO instanceof SavingsAccountDTO){

                SavingsAccountDTO savingsAccountDTO = (SavingsAccountDTO) accountDTO;
            }
        }

    }

    private String translateAccountType(String accountType){

        switch (accountType){
            case "SAVINGS":
                return "de Ahorros";
            case "CHECKING":
                return "Corriente";
            case "FIXED_TERM":
                return "a Plazo fijo";
            default:
                return "";
        }
    }
/*
    @Autowired
    private AccountRepository accountRepository;
    private AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @Autowired
    private AccountNumberGenerator accountNumberGenerator;

    public Account createAccount(AccountRequest request) {
        validateAccountCreation(request);

        Account account = accountMapper.toEntity(request);
        account.setAccountNumber(accountNumberGenerator.generateAccountNumber());

        return accountRepository.save(account);
    }

    public Account getAccountById(String id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
    }

    public List<Account> getAllAccounts(String customerId) {
        return customerId != null ?
                accountRepository.findByCustomerId(customerId) :
                accountRepository.findAll();
    }

    public Account updateAccount(String id, AccountRequest request) {
        Account account = getAccountById(id);
        accountMapper.updateEntity(request, account);
        return accountRepository.save(account);
    }

    public void deleteAccount(String id) {
        Account account = getAccountById(id);
        validateAccountDeletion(account);
        accountRepository.delete(account);
    }

    private void validateAccountCreation(AccountRequest request) {


        if ("SAVINGS".equals(request.getAccountType()) || "FIXED_TERM".equals(request.getAccountType())) {
            long count = accountRepository.countByCustomerIdAndAccountType(
                    request.getCustomerId(), request.getAccountType().toString());
            if (count > 0) {
                throw new BusinessRuleException("Customer already has a " +
                        translateAccountType(request.getAccountType().toString()) + " account");
            }
        }
    }

    private void validateAccountDeletion(Account account) {
        if (account.getBalance() > 0) {
            throw new BusinessRuleException("No se puede eliminar una cuenta con saldo positivo");
        }

    }

    private String translateAccountType(String accountType){

        switch (accountType){
            case "SAVINGS":
                return "de Ahorros";
            case "CHECKING":
                return "Corriente";
            case "FIXED_TERM":
                return "a Plazo fijo";
            default:
                return "";
        }
    }*/
}
