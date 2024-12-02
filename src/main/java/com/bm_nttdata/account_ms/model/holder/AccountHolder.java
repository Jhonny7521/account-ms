package com.bm_nttdata.account_ms.model.holder;

import com.bm_nttdata.account_ms.enums.HolderTypeEnum;
import com.bm_nttdata.account_ms.enums.SignatureTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa a un titular de cuenta bancaria.
 * Un titular de cuenta tiene derechos y responsabilidades sobre la cuenta,
 * incluyendo la capacidad de realizar operaciones según su tipo de titular y firma.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountHolder {

    private String documentNumber;
    private String name;
    private HolderTypeEnum holderType;
    private SignatureTypeEnum signatureType;

}
