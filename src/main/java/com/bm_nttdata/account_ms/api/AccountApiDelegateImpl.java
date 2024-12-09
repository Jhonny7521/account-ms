package com.bm_nttdata.account_ms.api;

import com.bm_nttdata.account_ms.entity.Account;
import com.bm_nttdata.account_ms.mapper.AccountMapper;
import com.bm_nttdata.account_ms.mapper.BankFeeMapper;
import com.bm_nttdata.account_ms.mapper.DailyBalanceMapper;
import com.bm_nttdata.account_ms.model.AccountRequestDTO;
import com.bm_nttdata.account_ms.model.AccountResponseDTO;
import com.bm_nttdata.account_ms.model.ApiResponseDTO;
import com.bm_nttdata.account_ms.model.BalanceResponseDTO;
import com.bm_nttdata.account_ms.model.BankFeeDto;
import com.bm_nttdata.account_ms.model.DailyBalanceDto;
import com.bm_nttdata.account_ms.model.DepositRequestDTO;
import com.bm_nttdata.account_ms.model.TransactionFeeRequestDTO;
import com.bm_nttdata.account_ms.model.TransactionFeeResponseDTO;
import com.bm_nttdata.account_ms.model.TransferRequestDTO;
import com.bm_nttdata.account_ms.model.WithdrawalRequestDTO;
import com.bm_nttdata.account_ms.service.AccountService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Implementación del delegado de la API de cuentas bancarias.
 * Maneja las peticiones HTTP recibidas por los endpoints de la API,
 * delegando la lógica de negocio al servicio correspondiente y
 * transformando las respuestas al formato requerido por la API.
 */
@Slf4j
@Component
public class AccountApiDelegateImpl implements AccountApiDelegate {

    @Autowired
    private AccountService accountService;
    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    private final BankFeeMapper bankFeeMapper = Mappers.getMapper(BankFeeMapper.class);
    private final DailyBalanceMapper dailyBalanceMapper =
            Mappers.getMapper(DailyBalanceMapper.class);

    @Override
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts(
            String customerId, String accountType) {
        log.info("Getting accounts for customer: {}", customerId);
        //return AccountApiDelegate.super.getAllAccounts(accountType);
        List<AccountResponseDTO> accounts = accountService.getAllAccounts(customerId, accountType)
                .stream()
                .map(accountMapper::entityAccountToAccountResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(accounts);
    }

    @Override
    public ResponseEntity<AccountResponseDTO> getAccountById(String id) {
        log.info("Getting account: {}", id);
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(accountMapper.entityAccountToAccountResponseDto(account));
    }

    @Override
    @CircuitBreaker(name = "allAccountsMethods", fallbackMethod = "createAccountFallback")
    public ResponseEntity<AccountResponseDTO> createAccount(AccountRequestDTO accountRequest) {
        log.info("Creating account for customer: {}", accountRequest.getCustomerId());
        Account account = accountService.createAccount(accountRequest);
        return ResponseEntity.ok(accountMapper.entityAccountToAccountResponseDto(account));
    }

    @Override
    public ResponseEntity<AccountResponseDTO> updateAccount(
            String id, AccountRequestDTO accountRequest) {
        log.info("Updating account: {}", id);
        Account account = accountService.updateAccount(id, accountRequest);
        return ResponseEntity.ok(accountMapper.entityAccountToAccountResponseDto(account));
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
    public ResponseEntity<List<DailyBalanceDto>> getAllDailyBalances(
            String id, LocalDate searchMonth) {
        log.info("Getting average balance for account: {}", id);
        List<DailyBalanceDto> dailyBalances = accountService.getDailyBalances(id, searchMonth)
                .stream()
                .map(dailyBalanceMapper::dailyBalanceToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dailyBalances);
    }

    @Override
    public ResponseEntity<TransactionFeeResponseDTO> checkTransactionFee(
            TransactionFeeRequestDTO transactionFeeRequest) {
        log.info("Checking the account transaction fee: {}", transactionFeeRequest.getAccountId());
        TransactionFeeResponseDTO responseDto =
                accountService.checkTransactionFee(transactionFeeRequest);
        return ResponseEntity.ok(responseDto);
    }

    @Override
    public ResponseEntity<ApiResponseDTO> depositToAccount(DepositRequestDTO depositRequest) {

        log.info("Making deposit to bank account: {}", depositRequest.getTargetAccountId());
        ApiResponseDTO responseDto = accountService.makeDepositAccount(depositRequest);
        return ResponseEntity.ok(responseDto);
    }

    @Override
    public ResponseEntity<ApiResponseDTO> withdrawalToAccount(
            WithdrawalRequestDTO withdrawalRequest) {
        log.info("Withdrawal from bank account: {}", withdrawalRequest.getSourceAccountId());
        ApiResponseDTO responseDto = accountService.makeWithdrawalAccount(withdrawalRequest);
        return ResponseEntity.ok(responseDto);
    }

    @Override
    public ResponseEntity<List<AccountResponseDTO>> getAccountBystatus(String status) {
        log.info("Getting bank accounts with status: {}", status);
        List<AccountResponseDTO> accountResponseList = accountService.getAccountsByStatus(status)
                .stream()
                .map(accountMapper::entityAccountToAccountResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(accountResponseList);
    }

    @Override
    public ResponseEntity<List<BankFeeDto>> getAllBankFees(
            String id, LocalDate startDate, LocalDate endDate) {
        log.info("Getting bank fees for account: {}", id);
        List<BankFeeDto> bankFees = accountService.getBankFees(id, startDate, endDate)
                .stream()
                .map(bankFeeMapper::bankFeeEntityToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bankFees);
    }

    @Override
    public ResponseEntity<ApiResponseDTO> bankTransfer(TransferRequestDTO transferRequest) {
        log.info("Transfer from account {} to account {}",
                transferRequest.getSourceAccountId(),
                transferRequest.getTargetAccountId());
        ApiResponseDTO responseDto = accountService.processBankTransfer(transferRequest);
        return ResponseEntity.ok(responseDto);
    }

    private ResponseEntity<AccountResponseDTO> createAccountFallback(
            AccountRequestDTO accountRequest, Exception e) {
        log.error("Fallback for create account: {}", e.getMessage());
        return new ResponseEntity(
                "We are experiencing some errors. Please try again later", HttpStatus.OK);
    }
}
