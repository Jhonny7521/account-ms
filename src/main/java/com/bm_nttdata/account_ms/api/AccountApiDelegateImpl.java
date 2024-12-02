package com.bm_nttdata.account_ms.api;

import com.bm_nttdata.account_ms.DTO.OperationResponseDTO;
import com.bm_nttdata.account_ms.entity.Account;
import com.bm_nttdata.account_ms.mapper.AccountMapper;
import com.bm_nttdata.account_ms.model.*;
import com.bm_nttdata.account_ms.service.impl.AccountServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AccountApiDelegateImpl implements AccountApiDelegate {

    @Autowired
    private AccountServiceImpl accountService;
    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @Override
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts(String customerId, String accountType) {
        log.info("Getting accounts for customer: {}", customerId);
        //return AccountApiDelegate.super.getAllAccounts(accountType);
        List<AccountResponseDTO> accounts = accountService.getAllAccounts(customerId, accountType)
                .stream()
                .map(accountMapper::entityAccountToAccountResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(accounts);
    }

    @Override
    public ResponseEntity<AccountResponseDTO> getAccountById(String id) {
        log.info("Getting account: {}", id);
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(accountMapper.entityAccountToAccountResponseDTO(account));
    }

    @Override
    public ResponseEntity<AccountResponseDTO> createAccount(AccountRequestDTO accountRequestDTO) {
        log.info("Creating account for customer: {}", accountRequestDTO.getCustomerId());
        Account account = accountService.createAccount(accountRequestDTO);
        return ResponseEntity.ok(accountMapper.entityAccountToAccountResponseDTO(account));
    }

    @Override
    public ResponseEntity<AccountResponseDTO> updateAccount(String id, AccountRequestDTO accountRequest) {
        log.info("Updating account: {}", id);
        Account account = accountService.updateAccount(id, accountRequest);
        return ResponseEntity.ok(accountMapper.entityAccountToAccountResponseDTO(account));
    }

    @Override
    public ResponseEntity<Void> deleteAccount(String id) {
        log.info("Deleting account: {}", id);
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<BalanceResponseDTO> getAccountBalance(String id) {
        log.info("Getting balance for account: {}", id);
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(accountMapper.toBalanceResponse(account));
    }

    @Override
    public ResponseEntity<TransactionFeeResponseDTO> checkTransactionFee(TransactionFeeRequestDTO transactionFeeRequestDTO) {
        log.info("Checking the account transaction fee: {}", transactionFeeRequestDTO.getAccountId());
        TransactionFeeResponseDTO responseDTO = accountService.checkTransactionFee(transactionFeeRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @Override
    public ResponseEntity<ApiResponseDTO> depositToAccount(DepositRequestDTO depositRequestDTO) {

        log.info("Making deposit to bank account: {}", depositRequestDTO.getTargetAccountId());
        ApiResponseDTO responseDTO = accountService.makeDepositAccount(depositRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @Override
    public ResponseEntity<ApiResponseDTO> withdrawalToAccount(WithdrawalRequestDTO withdrawalRequestDTO) {
        log.info("Withdrawal from bank account: {}", withdrawalRequestDTO.getSourceAccountId());
        ApiResponseDTO responseDTO = accountService.makeWithdrawalAccount(withdrawalRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

}
