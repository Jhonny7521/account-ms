package com.bm_nttdata.account_ms.entity;

import com.bm_nttdata.account_ms.enums.AccountTypeEnum;
import com.bm_nttdata.account_ms.enums.FeeTypeEnum;
import com.bm_nttdata.account_ms.enums.OperationTypeEnum;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Clase que representa una entidad de comisiones en el sistema bancario.
 * Esta clase maneja el almacenamiento y gestión de comisiones aplicadas
 * a un producto bancario.
 */
@Data
@Builder
@Document(collection = "fee")
public class BankFee {

    @Id
    private String id;
    private String accountId;
    private AccountTypeEnum accountType;
    private OperationTypeEnum operationType;
    private LocalDate date;
    private FeeTypeEnum feeType;
    private BigDecimal feeAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
