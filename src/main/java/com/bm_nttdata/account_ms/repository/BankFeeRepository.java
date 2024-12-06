package com.bm_nttdata.account_ms.repository;

import com.bm_nttdata.account_ms.entity.BankFee;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositorio para la gestión de comisiones bancarias en MongoDB.
 * Proporciona operaciones de acceso a datos para la entidad BankFee.
 */
public interface BankFeeRepository extends MongoRepository<BankFee, String> {
}
