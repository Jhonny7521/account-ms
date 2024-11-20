package com.bm_nttdata.account_ms.service;

import com.bm_nttdata.account_ms.entity.Account;
import com.bm_nttdata.account_ms.model.AccountDTO;

import java.util.List;

public interface IAccountService {

    List<Account> getAllAccounts(String customerId, String accountType);

    Account getAccountById(String id);

    Account createAccount(AccountDTO accountRequest);

    Account updateAccount(String id, Account request);

    void deleteAccount(String id);

}
