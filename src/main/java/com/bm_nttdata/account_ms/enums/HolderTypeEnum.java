package com.bm_nttdata.account_ms.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeración que representa los tipos de titulares de una cuenta bancaria.
 * Define si un titular es primario o secundario en una cuenta.
 */
public enum HolderTypeEnum {
    PRIMARY("PRIMARY"),
    SECONDARY("SECONDARY");

    private final String value;

    /**
     * Constructor del enum HolderTypeEnum.
     *
     * @param value Valor string que representa el tipo de titular
     */
    HolderTypeEnum(String value) {
        this.value = value;
    }

    /**
     * Obtiene el valor string del tipo de titular.
     *
     * @return El valor string asociado al tipo de titular
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Retorna la representación en string del tipo de titular.
     *
     * @return String que representa el tipo de titular
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Convierte un valor string a su correspondiente enum HolderTypeEnum.
     *
     * @param value Valor string a convertir
     * @return El enum HolderTypeEnum correspondiente al valor
     * @throws IllegalArgumentException si el valor no corresponde a ningún tipo de titular válido
     */
    @JsonCreator
    public static HolderTypeEnum fromValue(String value) {
        for (HolderTypeEnum b : HolderTypeEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected Value '" + value + "'");
    }
}
