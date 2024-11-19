package com.bm_nttdata.account_ms.service;
/*
import com.bm_nttdata.account_ms.entity.Account;
import com.bm_nttdata.account_ms.exception.AccountNotFoundException;
import com.bm_nttdata.account_ms.exception.BusinessRuleException;
import com.bm_nttdata.account_ms.mapper.AccountMapper;
import com.bm_nttdata.account_ms.model.AccountRequest;
import com.bm_nttdata.account_ms.repository.AccountRepository;
import com.bm_nttdata.account_ms.util.AccountNumberGenerator;*/
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
public class AccountService {
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
