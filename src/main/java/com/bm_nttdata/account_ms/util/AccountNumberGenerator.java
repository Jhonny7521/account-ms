package com.bm_nttdata.account_ms.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AccountNumberGenerator {
    private static final Random random = new Random();

    public String generateAccountNumber() {
        return String.format("%04d-%04d-%04d-%04d",
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000));
    }
}
