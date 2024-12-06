package com.bm_nttdata.account_ms.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeración que representa los tipos de comisiones aplicados a una cuenta bancaria.
 * Define si una comision es por mantenimiento, exceso de transacciones o pago retrasado.
 */
public enum FeeTypeEnum {
    MAINTENANCE_FEE("MAINTENANCE_FEE"),
    TRANSACTION_EXCESS("TRANSACTION_EXCESS");

    private final String value;

    /**
     * Constructor del enum FeeTypeEnum.
     *
     * @param value Valor string que representa el tipo de comisión
     */
    FeeTypeEnum(String value) {
        this.value = value;
    }

    /**
     * Obtiene el valor string del tipo de comisión.
     *
     * @return El valor string asociado al tipo de comisión
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Retorna la representación en string del tipo de comisión.
     *
     * @return String que representa el tipo de comisión
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Convierte un valor string a su correspondiente enum FeeTypeEnum.
     *
     * @param value Valor string a convertir
     * @return El enum FeeTypeEnum correspondiente al valor
     * @throws IllegalArgumentException si el valor no corresponde a ningún tipo de comisión válido
     */
    @JsonCreator
    public static FeeTypeEnum fromValue(String value) {
        for (FeeTypeEnum b : FeeTypeEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected Value '" + value + "'");
    }
}
