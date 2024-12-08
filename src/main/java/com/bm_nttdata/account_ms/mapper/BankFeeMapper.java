package com.bm_nttdata.account_ms.mapper;

import com.bm_nttdata.account_ms.entity.BankFee;
import com.bm_nttdata.account_ms.model.BankFeeDto;
import org.mapstruct.Mapper;

/**
 * Mapper para la conversión entre entidades y DTOs relacionados con comisiones bancarias.
 */
@Mapper(componentModel = "spring")
public interface BankFeeMapper {

    /**
     * Convierte una entidad BankFee a BankFeeDto.
     *
     * @param bankFee Entidad del comision bancaria
     * @return DTO con los datos de la comision bancaria
     */
    BankFeeDto bankFeeEntityToDto(BankFee bankFee);
}
