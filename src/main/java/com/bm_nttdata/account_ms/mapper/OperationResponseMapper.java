package com.bm_nttdata.account_ms.mapper;

import com.bm_nttdata.account_ms.DTO.OperationResponseDTO;
import com.bm_nttdata.account_ms.model.ApiResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OperationResponseMapper {

    ApiResponseDTO entityOperationResponseToApiResponseDTO(OperationResponseDTO operationResponseDTO);
}
