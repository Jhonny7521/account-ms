package com.bm_nttdata.account_ms.util;

import java.util.Random;
import org.springframework.stereotype.Component;

/**
 * Generador de números de cuenta bancaria.
 * Proporciona funcionalidad para crear números de cuenta únicos
 * en formato XXXX-XXXX-XXXX-XXXX donde X son dígitos aleatorios.
 */
@Component
public class AccountNumberGenerator {
    private static final Random random = new Random();

    /**
     * Genera un nuevo número de cuenta bancaria.
     * El formato generado es XXXX-XXXX-XXXX-XXXX donde cada X es un dígito aleatorio.
     *
     * @return String con el número de cuenta generado en formato XXXX-XXXX-XXXX-XXXX
     */
    public String generateAccountNumber() {
        return String.format("%04d-%04d-%04d-%04d",
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000),
                random.nextInt(10000));
    }
}
