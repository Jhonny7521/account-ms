package com.bm_nttdata.account_ms.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeración que representa los tipos de firma disponibles para cuentas bancarias.
 * Define si una firma debe ser individual o conjunta para operaciones bancarias.
 */
public enum SignatureTypeEnum {

    INDIVIDUAL("INDIVIDUAL"), JOINT("JOINT");

    private final String value;

    /**
     * Constructor del enum SignatureTypeEnum.
     *
     * @param value Valor string que representa el tipo de firma
     */
    SignatureTypeEnum(String value) {
        this.value = value;
    }

    /**
     * Obtiene el valor string del tipo de firma.
     *
     * @return El valor string asociado al tipo de firma
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Retorna la representación en string del tipo de firma.
     *
     * @return String que representa el tipo de firma
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Convierte un valor string a su correspondiente enum SignatureTypeEnum.
     *
     * @param value Valor string a convertir
     * @return El enum SignatureTypeEnum correspondiente al valor
     * @throws IllegalArgumentException si el valor no corresponde a ningún tipo de firma válido
     */
    @JsonCreator
    public static SignatureTypeEnum fromValue(String value) {
        for (SignatureTypeEnum b : SignatureTypeEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected Value '" + value + "'");
    }

}
