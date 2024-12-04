package com.bm_nttdata.account_ms.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase DTO para representar los datos esenciales
 * de una tarjeta de crédito obtenidos del microservicio de créditos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardDto {

    private String id;
    private String cardNumber;
    private BigDecimal creditLimit;
    private String status;
}
