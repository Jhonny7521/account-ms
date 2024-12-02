package com.bm_nttdata.account_ms.model.signer;

import com.bm_nttdata.account_ms.enums.SignatureTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa a un firmante autorizado de una cuenta bancaria.
 * Un firmante autorizado tiene permisos para realizar operaciones en la cuenta
 * según su tipo de firma y límite de transacción, sin ser titular de la misma.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizedSigner {

    private String documentNumber;
    private String name;
    private String position;
    private SignatureTypeEnum signatureType;
    private String transactionLimit;

}
