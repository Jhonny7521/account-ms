package com.bm_nttdata.account_ms.client;

import com.bm_nttdata.account_ms.dto.CreditCardDto;
import feign.FeignException;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Cliente Feign para la comunicación con el microservicio de créditoa.
 * Proporciona métodos para realizar operaciones relacionadas con la información de créditos
 * a través de llamadas HTTP REST.
 */
@FeignClient(name = "credit-ms", url = "${credit-service.url}")
public interface CreditClient {

    /**
     * Obtiene todas las tarjetas de crédito asociadas a un cliente específico.
     *
     * @param customerId identificador único del cliente cuyas tarjetas se desean consultar
     * @return lista de tarjetas de crédito asociadas al cliente
     * @throws FeignException cuando ocurre un error en la comunicación con el servicio
     */
    @GetMapping("/credit-cards")
    List<CreditCardDto> getAllCreditCards(@RequestParam(value = "customerId") String customerId);
}
