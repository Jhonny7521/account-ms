package com.bm_nttdata.account_ms.exception;

public class AccountNotFoundException extends RuntimeException{

    public AccountNotFoundException(String message) {
        super(message);
    }
}
