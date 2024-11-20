package com.bm_nttdata.account_ms.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SignatureTypeEnum {

    INDIVIDUAL("INDIVIDUAL"), JOINT("JOINT");

    private final String value;

    SignatureTypeEnum(String value){
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
    public static SignatureTypeEnum fromValue(String value){
        for (SignatureTypeEnum b : SignatureTypeEnum.values()){
            if (b.value.equals(value)){
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected Value '" + value + "'");
    }

}
