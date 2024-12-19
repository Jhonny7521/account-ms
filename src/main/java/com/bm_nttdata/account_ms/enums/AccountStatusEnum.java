package com.bm_nttdata.account_ms.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeración que representa los tipos de estatus de cuentas bancarias.
 * Define si una cuenta se encuentra Activa, Inactiva o Bloqueada.
 */
public enum AccountStatusEnum {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    BLOCKED("BLOCKED");

    private final String value;

    /**
     * Constructor del enum AccountStatusEnum.
     *
     * @param value Valor string que representa el tipo de estatus
     */
    AccountStatusEnum(String value) {
        this.value = value;
    }

    /**
     * Obtiene el valor string del tipo de estatus.
     *
     * @return El valor string asociado al tipo de estatus
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Retorna la representación en string del tipo de estatus.
     *
     * @return String que representa el tipo de estatus
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Convierte un valor string a su correspondiente enum AccountStatusEnum.
     *
     * @param value Valor string a convertir
     * @return El enum AccountStatusEnum correspondiente al valor
     * @throws IllegalArgumentException si el valor no corresponde a ningún tipo de estatus válido
     */
    @JsonCreator
    public static AccountStatusEnum fromValue(String value) {
        for (AccountStatusEnum b : AccountStatusEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
