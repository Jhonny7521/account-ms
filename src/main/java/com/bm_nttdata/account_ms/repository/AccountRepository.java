package com.bm_nttdata.account_ms.repository;

import com.bm_nttdata.account_ms.entity.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AccountRepository extends MongoRepository<Account, String> {
    List<Account> findByCustomerId(String customerId);
    List<Account> findByCustomerIdAndAccountType(String customerId, String accountType);
    boolean existsByCustomerIdAndAccountType(String customerId, String accountType);
    long countByCustomerIdAndAccountType(String customerId, String accountType);
}
