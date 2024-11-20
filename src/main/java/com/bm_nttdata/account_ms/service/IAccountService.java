package com.bm_nttdata.account_ms.service;

import com.bm_nttdata.account_ms.entity.Account;
import com.bm_nttdata.account_ms.model.AccountRequestDTO;

import java.util.List;

public interface IAccountService {

    List<Account> getAllAccounts(String customerId, String accountType);

    Account getAccountById(String id);

    Account createAccount(AccountRequestDTO accountRequest);

    Account updateAccount(String id, AccountRequestDTO accountRequest);

    void deleteAccount(String id);

}
