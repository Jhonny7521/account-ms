package com.bm_nttdata.account_ms.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum HolderTypeEnum {
    PRIMARY("PRIMARY"),
    SECONDARY("SECONDARY");

    private final String value;

    HolderTypeEnum(String value){
        this.value = value;
    }

    @JsonValue
    public String getValue(){
        return value;
    }

    @Override
    public String toString(){
        return String.valueOf(value);
    }

    @JsonCreator
    public static HolderTypeEnum fromValue(String value){
        for (HolderTypeEnum b : HolderTypeEnum.values()){
            if (b.value.equals(value)){
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected Value '" + value + "'");
    }
}
