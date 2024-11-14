package com.bm_nttdata.account_ms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "account")
public class Account {

    @Id
    private String id;
    private String customerId;
    private String accountType;
    private String accountNumber;
    private String currency;
    private Double balance;
    private Double maintenanceFee;
    private Integer maxMonthlyMovements;
    private Integer currentMonthMovements;
    private LocalDate withdrawalDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
