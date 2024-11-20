package com.bm_nttdata.account_ms.mapper;

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

    @Mapping(target = "accountType", source = "accountType")
    @Mapping(target = "accountHolders", source = "accountHolders")
    @Mapping(target = "authorizedSigners", source = "authorizedSigners")
    @Mapping(target = "monthlyMovementLimit", source = "monthlyMovementLimit")
    AccountResponseDTO entityAccountToAccountResponseDTO(Account account);

    Account accountRequestDtoToEntityAccount(AccountRequestDTO accountRequestDTO);

    List<AccountResponseDTO> entityAccountToAccountResponseDTOList(List<Account> accountList);

    List<Account> accountRequestDtoToEntityAccountList(List<AccountRequestDTO> accountRequestDTOList);

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
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntityAccountFromAccountRequestDto(AccountRequestDTO accountRequestDTO, @MappingTarget Account account);

    @Mapping(target = "accountId", source = "id")
    @Mapping(target = "availableBalance", source = "balance")
    @Mapping(target = "lastUpdateDate", source = "updatedAt")
    BalanceResponseDTO toBalanceResponse(Account account);
}
