package com.bm_nttdata.account_ms.mapper;

import com.bm_nttdata.account_ms.entity.Account;
import com.bm_nttdata.account_ms.enums.HolderTypeEnum;
import com.bm_nttdata.account_ms.enums.SignatureTypeEnum;
import com.bm_nttdata.account_ms.model.AccountHolderDTO;
import com.bm_nttdata.account_ms.model.AccountRequestDTO;
import com.bm_nttdata.account_ms.model.AccountResponseDTO;
import com.bm_nttdata.account_ms.model.AuthorizedSignerDTO;
import com.bm_nttdata.account_ms.model.BalanceResponseDTO;
import com.bm_nttdata.account_ms.model.holder.AccountHolder;
import com.bm_nttdata.account_ms.model.signer.AuthorizedSigner;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

/**
 * Mapper para la conversión entre entidades y DTOs relacionados con cuentas bancarias.
 */
@Mapper(componentModel = "spring")
public interface AccountMapper {
    /**
     * Convierte un AccountHolderDTO a una entidad AccountHolder.
     *
     * @param accountHolder DTO con los datos del titular de la cuenta
     * @return Entidad AccountHolder mapeada
     */
    @Mapping(
        target = "holderType",
        expression = "java(mapToModelHolderType(accountHolder.getHolderType()))")
    @Mapping(
        target = "signatureType",
        expression =
            "java(mapAccountHolderSignatureTypeToModel(accountHolder.getSignatureType()))"
    )
    AccountHolder toModelAccountHolder(AccountHolderDTO accountHolder);

    /**
     * Convierte una entidad AccountHolder a AccountHolderDTO.
     *
     * @param accountHolder Entidad del titular de la cuenta
     * @return DTO con los datos del titular mapeados
     */
    @Mapping(
        target = "holderType",
        expression = "java(mapToDtoHolderType(accountHolder.getHolderType()))")
    @Mapping(
        target = "signatureType",
        expression =
            "java(mapAccountHolderSignatureTypeToDto(accountHolder.getSignatureType()))")
    AccountHolderDTO toAccountHolderDto(AccountHolder accountHolder);

    /**
     * Convierte un AuthorizedSignerDTO a una entidad AuthorizedSigner.
     *
     * @param authorizedSigner DTO con los datos del firmante autorizado
     * @return Entidad AuthorizedSigner mapeada
     */
    @Mapping(
        target = "signatureType",
        expression =
            "java(mapAuthorizedSignerSignatureTypeToModel(authorizedSigner.getSignatureType()))"
    )
    AuthorizedSigner toModelAuthorizedSigner(AuthorizedSignerDTO authorizedSigner);

    /**
     * Convierte una entidad AuthorizedSigner a AuthorizedSignerDTO.
     *
     * @param authorizedSigner Entidad del firmante autorizado
     * @return DTO con los datos del firmante mapeados
     */
    @Mapping(
        target = "signatureType",
        expression =
            "java(mapAuthorizedSignerSignatureTypeToDto(authorizedSigner.getSignatureType()))")
    AuthorizedSignerDTO toAuthorizedSignerDto(AuthorizedSigner authorizedSigner);

    // Métodos auxiliares para mapeo de enums

    /**
     * Mapea el tipo de titular desde el DTO al modelo.
     *
     * @param holderType Tipo de titular del DTO
     * @return Tipo de titular del modelo
     */
    default HolderTypeEnum mapToModelHolderType(AccountHolderDTO.HolderTypeEnum holderType) {
        if (holderType == null) {
            return null;
        }
        return HolderTypeEnum.valueOf(holderType.name());
    }

    /**
     * Mapea el tipo de titular desde el modelo al DTO.
     *
     * @param holderType Tipo de titular del modelo
     * @return Tipo de titular del DTO
     */
    default AccountHolderDTO.HolderTypeEnum mapToDtoHolderType(HolderTypeEnum holderType) {
        if (holderType == null) {
            return null;
        }
        return AccountHolderDTO.HolderTypeEnum.valueOf(holderType.name());
    }

    // Métodos auxiliares para SignatureType de AccountHolder

    /**
     * Mapea el tipo de firma del titular desde el DTO al modelo.
     *
     * @param signatureType Tipo de firma del DTO
     * @return Tipo de firma del modelo
     */
    default SignatureTypeEnum mapAccountHolderSignatureTypeToModel(
            AccountHolderDTO.SignatureTypeEnum signatureType) {
        if (signatureType == null) {
            return null;
        }
        return SignatureTypeEnum.valueOf(signatureType.name());
    }

    /**
     * Mapea el tipo de firma del titular desde el modelo al DTO.
     *
     * @param signatureType Tipo de firma del modelo
     * @return Tipo de firma del DTO
     */
    default AccountHolderDTO.SignatureTypeEnum mapAccountHolderSignatureTypeToDto(
            SignatureTypeEnum signatureType) {
        if (signatureType == null) {
            return null;
        }
        return AccountHolderDTO.SignatureTypeEnum.valueOf(signatureType.name());
    }

    /**
     * Mapea el tipo de firma del firmante autorizado desde el DTO al modelo.
     *
     * @param signatureType Tipo de firma del DTO
     * @return Tipo de firma del modelo
     */
    default SignatureTypeEnum mapAuthorizedSignerSignatureTypeToModel(
            AuthorizedSignerDTO.SignatureTypeEnum signatureType) {
        if (signatureType == null) {
            return null;
        }
        return SignatureTypeEnum.valueOf(signatureType.name());
    }

    /**
     * Mapea el tipo de firma del firmante autorizado desde el modelo al DTO.
     *
     * @param signatureType Tipo de firma del modelo
     * @return Tipo de firma del DTO
     */
    default AuthorizedSignerDTO.SignatureTypeEnum mapAuthorizedSignerSignatureTypeToDto(
            SignatureTypeEnum signatureType) {
        if (signatureType == null) {
            return null;
        }
        return AuthorizedSignerDTO.SignatureTypeEnum.valueOf(signatureType.name());
    }

    /**
     * Convierte una entidad Account a AccountResponseDTO.
     *
     * @param account Entidad de la cuenta bancaria
     * @return DTO de respuesta con los datos de la cuenta
     */
    @Mapping(target = "accountType", source = "accountType")
    @Mapping(target = "accountHolders", source = "accountHolders")
    @Mapping(target = "authorizedSigners", source = "authorizedSigners")
    @Mapping(target = "monthlyMovementLimit", source = "monthlyMovementLimit")
    AccountResponseDTO entityAccountToAccountResponseDto(Account account);

    /**
     * Convierte un AccountRequestDTO a una entidad Account.
     *
     * @param accountRequest DTO con los datos de la solicitud de cuenta
     * @return Entidad Account mapeada
     */
    @Mapping(target = "balance",
            source = "initialBalance",
            qualifiedByName = "mapInitialBalance")
    Account accountRequestDtoToEntityAccount(AccountRequestDTO accountRequest);

    /**
     * Convierte una lista de entidades Account a lista de AccountResponseDTO.
     *
     * @param accountList Lista de entidades Account
     * @return Lista de DTOs de respuesta
     */
    List<AccountResponseDTO> entityAccountToAccountResponseDtoList(List<Account> accountList);

    /**
     * Convierte una lista de AccountRequestDTO a lista de entidades Account.
     *
     * @param accountRequestList Lista de DTOs de solicitud
     * @return Lista de entidades Account mapeadas
     */
    List<Account> accountRequestDtoToEntityAccountList(List<AccountRequestDTO> accountRequestList);

    /**
     * Convierte una lista de AccountHolder a lista de AccountHolderDTO.
     *
     * @param accountHolders Lista de titulares de cuenta
     * @return Lista de DTOs de titulares
     */
    List<AccountHolderDTO> mapAccountHolders(List<AccountHolder> accountHolders);

    /**
     * Convierte una lista de AuthorizedSigner a lista de AuthorizedSignerDTO.
     *
     * @param authorizedSigners Lista de firmantes autorizados
     * @return Lista de DTOs de firmantes
     */
    List<AuthorizedSignerDTO> mapAuthorizedSigners(List<AuthorizedSigner> authorizedSigners);

    /**
     * Convierte una lista de AccountHolderDTO a lista de AccountHolder.
     *
     * @param accountHolders Lista de DTOs de titulares
     * @return Lista de entidades de titulares
     */
    List<AccountHolder> mapToEntityAccountHolders(List<AccountHolderDTO> accountHolders);

    /**
     * Convierte una lista de AuthorizedSignerDTO a lista de AuthorizedSigner.
     *
     * @param authorizedSigners Lista de DTOs de firmantes autorizados
     * @return Lista de entidades de firmantes autorizados
     */
    List<AuthorizedSigner> mapToEntityAuthorizedSigners(
            List<AuthorizedSignerDTO> authorizedSigners);

    /**
     * Convierte un LocalDateTime a OffsetDateTime en UTC.
     *
     * @param localDateTime Fecha y hora local
     * @return Fecha y hora con zona horaria UTC
     */
    default OffsetDateTime map(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZoneOffset.UTC);
    }

    /**
     * Convierte un OffsetDateTime a LocalDateTime.
     *
     * @param offsetDateTime Fecha y hora con zona horaria
     * @return Fecha y hora local
     */
    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.toLocalDateTime();
    }

    /**
     * Actualiza una entidad Account existente con datos de un AccountRequestDTO.
     *
     * @param accountRequest DTO con los datos de actualización
     * @param account Entidad Account a actualizar
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntityAccountFromAccountRequestDto(
            AccountRequestDTO accountRequest, @MappingTarget Account account);

    /**
     * Convierte una entidad Account a BalanceResponseDTO.
     *
     * @param account Entidad de la cuenta bancaria
     * @return DTO con la información del balance
     */
    @Mapping(target = "accountId", source = "id")
    @Mapping(target = "availableBalance", source = "balance")
    @Mapping(target = "lastUpdateDate", source = "updatedAt")
    BalanceResponseDTO toBalanceResponse(Account account);

    /**
     * Convierte un valor de saldo inicial a un valor seguro para su uso.
     *
     * @param initialBalance el saldo inicial que puede ser nulo.
     * @return el saldo inicial si no es nulo, o cero en caso contrario.
     */
    @Named("mapInitialBalance")
    default BigDecimal mapInitialBalance(BigDecimal initialBalance) {
        if (initialBalance == null) {
            return BigDecimal.ZERO;
        }
        return initialBalance;
    }
}
