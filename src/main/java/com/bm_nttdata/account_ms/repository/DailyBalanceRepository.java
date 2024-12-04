package com.bm_nttdata.account_ms.repository;

import com.bm_nttdata.account_ms.entity.DailyBalance;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositorio para la gestión de saldos diarios de cuentas de ahorro VIP en MongoDB.
 * Proporciona operaciones de acceso a datos para la entidad DailyBalance.
 */
public interface DailyBalanceRepository extends MongoRepository<DailyBalance, String> {

    /**
     * Busca todos los saldos diarios de una cuenta en un período específico.
     *
     * @param accountId Identificador único de la cuenta
     * @param startDate fecha inicial del período de consulta
     * @param endDate fecha final del período de consulta
     * @return Lista de saldos diarios del cliente
     */
    List<DailyBalance> findByAccountIdAndDateBetween(
            String accountId, LocalDate startDate, LocalDate endDate);
}
