package com.bm_nttdata.account_ms.exception;

public class ApiInvalidRequestException extends RuntimeException{

    public ApiInvalidRequestException(String message) {
        super(message);
    }
}
