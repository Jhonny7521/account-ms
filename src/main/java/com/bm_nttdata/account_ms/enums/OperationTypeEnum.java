package com.bm_nttdata.account_ms.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeración que representa los tipos de comisiones aplicados a una cuenta bancaria.
 * Define si una comision es por mantenimiento, exceso de transacciones o pago retrasado.
 */
public enum OperationTypeEnum {
    DEPOSIT("DEPOSIT"),
    WITHDRAWAL("WITHDRAWAL"),
    MAINTENANCE("MAINTENANCE");

    private final String value;

    /**
     * Constructor del enum OperationTypeEnum.
     *
     * @param value Valor string que representa el tipo de operacion
     */
    OperationTypeEnum(String value) {
        this.value = value;
    }

    /**
     * Obtiene el valor string del tipo de operacion.
     *
     * @return El valor string asociado al tipo de operacion
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Retorna la representación en string del tipo de operacion.
     *
     * @return String que representa el tipo de operacion
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Convierte un valor string a su correspondiente enum OperationTypeEnum.
     *
     * @param value Valor string a convertir
     * @return El enum OperationTypeEnum correspondiente al valor
     * @throws IllegalArgumentException si el valor no corresponde a ningún tipo de operación válido
     */
    @JsonCreator
    public static OperationTypeEnum fromValue(String value) {
        for (OperationTypeEnum b : OperationTypeEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected Value '" + value + "'");
    }
}
