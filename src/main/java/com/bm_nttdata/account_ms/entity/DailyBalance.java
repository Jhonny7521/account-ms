package com.bm_nttdata.account_ms.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Clase que representa una entidad de balance diario en el sistema bancario.
 * Esta clase maneja el almacenamiento y gestión de los balances diarios
 * de cuentas de ahorro VIP.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "daily_balances")
public class DailyBalance {

    @Id
    private String id;
    private String accountId;
    private LocalDate date;
    private BigDecimal balance;

}
