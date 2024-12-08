package com.bm_nttdata.account_ms.repository;

import com.bm_nttdata.account_ms.entity.BankFee;
import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositorio para la gestión de comisiones bancarias en MongoDB.
 * Proporciona operaciones de acceso a datos para la entidad BankFee.
 */
public interface BankFeeRepository extends MongoRepository<BankFee, String> {

    /**
     * Busca todos los saldos diarios de una cuenta en un período específico.
     *
     * @param accountId Identificador único de la cuenta
     * @param startDate fecha inicial del período de consulta
     * @param endDate fecha final del período de consulta
     * @return Lista de saldos diarios del cliente
     */
    List<BankFee> findByAccountIdAndDateBetween(
            String accountId, Date startDate, Date endDate);
}
