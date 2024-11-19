package com.bm_nttdata.account_ms.model.holder;

import com.bm_nttdata.account_ms.enums.HolderTypeEnum;
import com.bm_nttdata.account_ms.enums.SignatureTypeEnum;
import lombok.*;

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
