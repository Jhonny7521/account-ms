package com.bm_nttdata.account_ms.model.signer;

import com.bm_nttdata.account_ms.enums.SignatureTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
