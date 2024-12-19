package com.bm_nttdata.account_ms.mapper;

import com.bm_nttdata.account_ms.entity.DailyBalance;
import com.bm_nttdata.account_ms.model.DailyBalanceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para la conversión entre entidades y DTOs relacionados con saldos diarios.
 */
@Mapper(componentModel = "spring")
public interface DailyBalanceMapper {

    /**
     * Convierte una entidad DailyBalance a DailyBalanceDto.
     *
     * @param dailyBalance Entidad del saldo diario
     * @return DTO con los datos del saldo diario
     */
    @Mapping(target = "balanceAmount", source = "balance")
    @Mapping(target = "balanceDate", source = "date")
    DailyBalanceDto dailyBalanceToDto(DailyBalance dailyBalance);

}
