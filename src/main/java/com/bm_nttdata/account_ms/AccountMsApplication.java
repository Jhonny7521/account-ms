package com.bm_nttdata.account_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class AccountMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountMsApplication.class, args);
	}

}
