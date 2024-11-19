package com.bm_nttdata.account_ms.mapper;
/*
import com.bm_nttdata.account_ms.entity.Account;
import com.bm_nttdata.account_ms.model.AccountRequest;
import com.bm_nttdata.account_ms.model.AccountResponse;
import com.bm_nttdata.account_ms.model.BalanceResponse;*/
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AccountMapper {
/*
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "balance", source = "initialBalance")
    @Mapping(target = "currentMonthMovements", constant = "0")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Account toEntity(AccountRequest request);

    AccountResponse toResponse(Account account);

    @Mapping(target = "accountId", source = "id")
    @Mapping(target = "availableBalance", source = "balance")
    @Mapping(target = "lastUpdateDate", source = "updatedAt")
    BalanceResponse toBalanceResponse(Account account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntity(AccountRequest request, @MappingTarget Account account);

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
*/
}
