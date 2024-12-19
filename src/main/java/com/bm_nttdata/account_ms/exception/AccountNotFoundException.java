package com.bm_nttdata.account_ms.exception;

/**
 * Excepción que se lanza cuando no se encuentra una cuenta bancaria específica.
 * Esta excepción es utilizada para manejar casos donde se intenta acceder a una
 * cuenta que no existe.
 */
public class AccountNotFoundException extends RuntimeException {

    /**
     * Construye una nueva excepción de cuenta no encontrada con el mensaje especificado.
     *
     * @param message Mensaje con la razón por la que no se encontró la cuenta
     */
    public AccountNotFoundException(String message) {
        super(message);
    }
}
