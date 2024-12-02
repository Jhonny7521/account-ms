package com.bm_nttdata.account_ms.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeración que representa los tipos de cuentas bancarias.
 * Define si una cuenta es de Ahorro, Corriente o Plazo fijo.
 */
public enum AccountTypeEnum {
    SAVINGS("SAVINGS"),
    CHECKING("CHECKING"),
    FIXED_TERM("FIXED_TERM");

    public final String value;

    /**
     * Constructor del enum AccountTypeEnum.
     *
     * @param value Valor string que representa el tipo de cuenta
     */
    AccountTypeEnum(String value) {
        this.value = value;
    }

    /**
     * Obtiene el valor string del tipo de cuenta.
     *
     * @return El valor string asociado al tipo de cuenta
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Retorna la representación en string del tipo de cuenta.
     *
     * @return String que representa el tipo de cuenta
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Convierte un valor string a su correspondiente enum AccountTypeEnum.
     *
     * @param value Valor string a convertir
     * @return El enum AccountTypeEnum correspondiente al valor
     * @throws IllegalArgumentException si el valor no corresponde a ningún tipo de cuenta válido
     */
    @JsonCreator
    public static AccountTypeEnum fromValue(String value) {
        for (AccountTypeEnum b : AccountTypeEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
