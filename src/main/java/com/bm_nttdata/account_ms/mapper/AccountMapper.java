package com.bm_nttdata.account_ms.mapper;
/*
import com.bm_nttdata.account_ms.entity.Account;
import com.bm_nttdata.account_ms.model.AccountRequest;
import com.bm_nttdata.account_ms.model.AccountResponse;
import com.bm_nttdata.account_ms.model.BalanceResponse;*/
import com.bm_nttdata.account_ms.entity.Account;
import com.bm_nttdata.account_ms.enums.HolderTypeEnum;
import com.bm_nttdata.account_ms.enums.SignatureTypeEnum;
import com.bm_nttdata.account_ms.model.*;
import com.bm_nttdata.account_ms.model.holder.AccountHolder;
import com.bm_nttdata.account_ms.model.signer.AuthorizedSigner;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "holderType", expression = "java(mapToModelHolderType(accountHolderDTO.getHolderType()))")
    @Mapping(target = "signatureType", expression = "java(mapAccountHolderSignatureTypeToModel(accountHolderDTO.getSignatureType()))")
    AccountHolder toModelAccountHolder(AccountHolderDTO accountHolderDTO);
    @Mapping(target = "holderType", expression = "java(mapToDTOHolderType(accountHolder.getHolderType()))")
    @Mapping(target = "signatureType", expression = "java(mapAccountHolderSignatureTypeToDTO(accountHolder.getSignatureType()))")
    AccountHolderDTO toAccountHolderDTO(AccountHolder accountHolder);
    @Mapping(target = "signatureType", expression = "java(mapAuthorizedSignerSignatureTypeToModel(authorizedSignerDTO.getSignatureType()))")
    AuthorizedSigner toModelAuthorizedSigner(AuthorizedSignerDTO authorizedSignerDTO);
    @Mapping(target = "signatureType", expression = "java(mapAuthorizedSignerSignatureTypeToDTO(authorizedSigner.getSignatureType()))")
    AuthorizedSignerDTO toAuthorizedSignerDTO(AuthorizedSigner authorizedSigner);

    // Métodos auxiliares para mapeo de enums
    default HolderTypeEnum mapToModelHolderType(AccountHolderDTO.HolderTypeEnum holderType) {
        if (holderType == null) return null;
        return HolderTypeEnum.valueOf(holderType.name());
    }

    default AccountHolderDTO.HolderTypeEnum mapToDTOHolderType(HolderTypeEnum holderType) {
        if (holderType == null) return null;
        return AccountHolderDTO.HolderTypeEnum.valueOf(holderType.name());
    }

    // Métodos auxiliares para SignatureType de AccountHolder
    default SignatureTypeEnum mapAccountHolderSignatureTypeToModel(AccountHolderDTO.SignatureTypeEnum signatureType) {
        if (signatureType == null) return null;
        return SignatureTypeEnum.valueOf(signatureType.name());
    }

    default AccountHolderDTO.SignatureTypeEnum mapAccountHolderSignatureTypeToDTO(SignatureTypeEnum signatureType) {
        if (signatureType == null) return null;
        return AccountHolderDTO.SignatureTypeEnum.valueOf(signatureType.name());
    }

    // Métodos auxiliares para SignatureType de AuthorizedSigner
    default SignatureTypeEnum mapAuthorizedSignerSignatureTypeToModel(AuthorizedSignerDTO.SignatureTypeEnum signatureType) {
        if (signatureType == null) return null;
        return SignatureTypeEnum.valueOf(signatureType.name());
    }

    default AuthorizedSignerDTO.SignatureTypeEnum mapAuthorizedSignerSignatureTypeToDTO(SignatureTypeEnum signatureType) {
        if (signatureType == null) return null;
        return AuthorizedSignerDTO.SignatureTypeEnum.valueOf(signatureType.name());
    }

    @Named("toSavingsAccountDTO")
    @Mapping(target = "accountType", constant = "SAVINGS")
    @Mapping(target = "accountHolders", source = "accountHolders")
    @Mapping(target = "authorizedSigners", source = "authorizedSigners")
    @Mapping(target = "monthlyMovementLimit", source = "maxMonthlyMovements")
    SavingsAccountDTO toSavingsAccountDTO(Account account);

    @Named("toCheckingAccountDTO")
    @Mapping(target = "accountType", constant = "CHECKING")
    @Mapping(target = "accountHolders", source = "accountHolders")
    @Mapping(target = "authorizedSigners", source = "authorizedSigners")
    CheckingAccountDTO toCheckingAccountDTO(Account account);

    @Named("toFixedTermAccountDTO")
    @Mapping(target = "accountType", constant = "FIXED_TERM")
    @Mapping(target = "accountHolders", source = "accountHolders")
    @Mapping(target = "authorizedSigners", source = "authorizedSigners")
    @Mapping(target = "withdrawalDay", source = "withdrawalDay")
    FixedTermAccountDTO toFixedTermAccountDTO(Account account);

    default AccountDTO toResponse(Account account){

        if (account == null){
            return null;
        }

        return switch (account.getAccountType()){
            case SAVINGS -> toSavingsAccountDTO(account);
            case CHECKING -> toCheckingAccountDTO(account);
            case FIXED_TERM -> toFixedTermAccountDTO(account);
        };
    }

    @SubclassMapping(source = SavingsAccountDTO.class, target = Account.class)
    @Mapping(target = "accountType", constant = "SAVINGS")
    @Mapping(target = "maxMonthlyMovements", source = "monthlyMovementLimit")
    Account savingsAccountToEntityAccount(SavingsAccountDTO savingsAccountDTO);

    @SubclassMapping(source = CheckingAccountDTO.class, target = Account.class)
    @Mapping(target = "accountType", constant = "CHECKING")
    Account checkingToEntity(CheckingAccountDTO checkingAccountDTO);

    @SubclassMapping(source = FixedTermAccountDTO.class, target = Account.class)
    @Mapping(target = "accountType", constant = "FIXED_TERM")
    @Mapping(target = "withdrawalDay", source = "withdrawalDay")
    Account fixedTermToEntity(FixedTermAccountDTO fixedTermAccountDTO);

    // Método para manejar las conversiones de listas
    List<AccountHolderDTO> mapAccountHolders(List<AccountHolder> accountHolders);
    List<AuthorizedSignerDTO> mapAuthorizedSigners(List<AuthorizedSigner> authorizedSigners);
    List<AccountHolder> mapToEntityAccountHolders(List<AccountHolderDTO> accountHolderDTOS);
    List<AuthorizedSigner> mapToEntityAuthorizedSigners(List<AuthorizedSignerDTO> authorizedSignerDTOS);

    default OffsetDateTime map(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.toLocalDateTime();
    }
    // Método After Mapping para ajustes posteriores al mapeo
//    @AfterMapping
//    default void afterMapping(@MappingTarget Account target) {
//        if (target.getCreatedAt() == null) {
//            target.setCreatedAt(LocalDateTime.now());
//        }
//        target.setUpdatedAt(LocalDateTime.now());
//    }

}
