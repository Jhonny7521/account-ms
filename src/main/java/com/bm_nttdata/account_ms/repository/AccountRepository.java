package com.bm_nttdata.account_ms.repository;

import com.bm_nttdata.account_ms.entity.Account;
import com.bm_nttdata.account_ms.enums.AccountTypeEnum;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositorio para la gestión de cuentas bancarias en MongoDB.
 * Proporciona operaciones de acceso a datos para la entidad Account.
 */
public interface AccountRepository extends MongoRepository<Account, String> {

    /**
     * Busca todas las cuentas bancarias asociadas a un cliente específico.
     *
     * @param customerId Identificador único del cliente
     * @return Lista de cuentas bancarias del cliente
     */
    List<Account> findByCustomerId(String customerId);

    /**
     * Busca todas las cuentas bancarias de un cliente filtradas por tipo de cuenta.
     *
     * @param customerId Identificador único del cliente
     * @param accountType Tipo de cuenta bancaria a buscar
     * @return Lista de cuentas bancarias que coinciden con los criterios
     */
    List<Account> findByCustomerIdAndAccountType(String customerId, String accountType);

    /**
     * Verifica si existe una cuenta bancaria para un cliente y tipo específico.
     *
     * @param customerId Identificador único del cliente
     * @param accountType Tipo de cuenta bancaria a verificar
     * @return true si existe una cuenta con los criterios especificados, false en caso contrario
     */
    boolean existsByCustomerIdAndAccountType(String customerId, String accountType);

    /**
     * Cuenta el número de cuentas bancarias de un tipo específico que tiene un cliente.
     *
     * @param customerId Identificador único del cliente
     * @param accountType Tipo de cuenta bancaria a contar
     * @return Número de cuentas que coinciden con los criterios
     */
    long countByCustomerIdAndAccountType(String customerId, String accountType);

    /**
     * Busca todas las cuentas bancarias por el tipo de cuenta.
     *
     * @param accountType Tipo de cuenta bancaria a buscar
     * @return Lista de cuentas bancarias que coinciden con los criterios
     */
    List<Account> findAllByAccountType(AccountTypeEnum accountType);
}
