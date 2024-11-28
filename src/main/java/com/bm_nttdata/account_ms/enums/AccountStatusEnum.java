package com.bm_nttdata.account_ms.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeración que representa los posibles estados de una cuenta.
 *
 * Estados disponibles:
 * - ACTIVE: La cuenta está activa.
 * - INACTIVE: La cuenta está inactiva.
 * - BLOCKED: La cuenta está bloqueada.
 */
public enum AccountStatusEnum {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    BLOCKED("BLOCKED");

    private final String value;

    AccountStatusEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

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
