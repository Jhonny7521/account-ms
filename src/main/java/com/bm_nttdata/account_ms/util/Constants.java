package com.bm_nttdata.account_ms.util;

import java.math.BigDecimal;

/**
 * Clase de constantes para el microservicio de cuentas bancarias.
 * Contiene valores constantes utilizados en diferentes partes de la aplicación
 * para mantener la consistencia y facilitar el mantenimiento.
 */
public class Constants {

    public static final double AMOUNT_TRANSACTION_FEE = 5.0;
    public static final BigDecimal VIP_MINIMUM_OPENING_BALANCE = new BigDecimal("1000.00");
    public static final BigDecimal VIP_MINIMUM_DAILY_AVERAGE = new BigDecimal("1000.00");

}
