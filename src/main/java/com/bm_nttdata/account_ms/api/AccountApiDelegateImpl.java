package com.bm_nttdata.account_ms.api;

import com.bm_nttdata.account_ms.entity.Account;
import com.bm_nttdata.account_ms.mapper.AccountMapper;
import com.bm_nttdata.account_ms.model.AccountRequest;
import com.bm_nttdata.account_ms.model.AccountResponse;
import com.bm_nttdata.account_ms.model.BalanceResponse;
import com.bm_nttdata.account_ms.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AccountApiDelegateImpl implements AccountApiDelegate {

    @Autowired
    private AccountService accountService;
    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @Override
    public ResponseEntity<AccountResponse> createAccount(AccountRequest accountRequest) {
        log.info("Creating account for customer: {}", accountRequest.getCustomerId());
        Account account = accountService.createAccount(accountRequest);
        return ResponseEntity.ok(accountMapper.toResponse(account));
    }

    @Override
    public ResponseEntity<Void> deleteAccount(String id) {
        log.info("Deleting account: {}", id);
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<AccountResponse>> getAllAccounts(String customerId) {
        log.info("Getting accounts for customer: {}", customerId);
        List<AccountResponse> accounts = accountService.getAllAccounts(customerId)
                .stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(accounts);
    }

    @Override
    public ResponseEntity<AccountResponse> getAccountById(String id) {
        log.info("Getting account: {}", id);
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(accountMapper.toResponse(account));
    }

    @Override
    public ResponseEntity<BalanceResponse> getAccountBalance(String id) {
        log.info("Getting balance for account: {}", id);
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(accountMapper.toBalanceResponse(account));
    }

    @Override
    public ResponseEntity<AccountResponse> updateAccount(String id, AccountRequest accountRequest) {
        log.info("Updating account: {}", id);
        Account account = accountService.updateAccount(id, accountRequest);
        return ResponseEntity.ok(accountMapper.toResponse(account));
    }
}
