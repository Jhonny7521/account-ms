package com.bm_nttdata.account_ms.service;

import com.bm_nttdata.account_ms.entity.Account;
import com.bm_nttdata.account_ms.entity.BankFee;
import com.bm_nttdata.account_ms.entity.DailyBalance;
import com.bm_nttdata.account_ms.model.AccountRequestDTO;
import com.bm_nttdata.account_ms.model.ApiResponseDTO;
import com.bm_nttdata.account_ms.model.DepositRequestDTO;
import com.bm_nttdata.account_ms.model.TransactionFeeRequestDTO;
import com.bm_nttdata.account_ms.model.TransactionFeeResponseDTO;
import com.bm_nttdata.account_ms.model.TransferRequestDTO;
import com.bm_nttdata.account_ms.model.WithdrawalRequestDTO;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz que define los servicios de gestión de cuentas bancarias.
 * Define las operaciones disponibles para administrar cuentas bancarias,
 * incluyendo consultas, creación, actualización, eliminación y operaciones
 * transaccionales como depósitos, retiros y transferencias bancarias.
 */
public interface AccountService {

    /**
     * Retorna todas las cuentas bancarias de un cliente.
     *
     * @param customerId Identificador único del cliente
     * @param accountType Tipo de cuenta bancaria a filtrar
     * @return Lista de cuentas bancarias que coinciden con los criterios de búsqueda
     */
    List<Account> getAllAccounts(String customerId, String accountType);

    /**
     * Retorna una cuenta bancaria obtenida por el id.
     *
     * @param id Identificador único de la cuenta bancaria
     * @return Cuenta bancaria encontrada
     */
    Account getAccountById(String id);

    /**
     * Crea una nueva cuenta bancaria en base a las reglas del negocio.
     *
     * @param accountRequest Objeto DTO con los datos necesarios para crear la cuenta
     * @return Nueva cuenta bancaria creada
     */
    Account createAccount(AccountRequestDTO accountRequest);

    /**
     * Actualiza datos globales de una cuenta bancaria.
     *
     * @param id Identificador único de la cuenta a actualizar
     * @param accountRequest Objeto DTO con los datos a actualizar
     * @return Cuenta bancaria actualizada
     */
    Account updateAccount(String id, AccountRequestDTO accountRequest);

    /**
     * Elimina una cuenta bancaria que no tenga saldo.
     *
     * @param id Identificador único de la cuenta a eliminar
     */
    void deleteAccount(String id);

    /**
     * Retorna todas los saldos diarios de una cuenta bancaria.
     *
     * @param accountId Identificador único de la cuenta
     * @return Lista de saldos diarios que coinciden con los criterios de búsqueda
     */
    List<DailyBalance> getDailyBalances(String accountId, LocalDate searchMonth);

    /**
     * Verifica Si la cuenta supera sus movimientos mensuales, para aplicar cargo de comision.
     *
     * @param transactionFeeRequest Objeto DTO con los datos necesarios para verificar la comisión
     * @return Objeto DTO con la respuesta sobre la aplicación de comisiones
     */
    TransactionFeeResponseDTO checkTransactionFee(
            TransactionFeeRequestDTO transactionFeeRequest);

    /**
     * Realiza un deposito verificando si se realiza el pago correspondiente de la
     * comision por superar limite de transacciones mensuales.
     *
     * @param depositRequest Objeto DTO con los datos necesarios para realizar el depósito
     * @return Objeto DTO con la respuesta del resultado de la operación
     */
    ApiResponseDTO makeDepositAccount(DepositRequestDTO depositRequest);

    /**
     * Realiza un retiro verificando si se realiza el pago correspondiente de la comision
     * por superar limite de transacciones mensuales.
     *
     * @param withdrawalRequest Objeto DTO con los datos necesarios para realizar el retiro
     * @return Objeto DTO con la respuesta del resultado de la operación
     */
    ApiResponseDTO makeWithdrawalAccount(WithdrawalRequestDTO withdrawalRequest);

    /**
     * Procesa una transferencia bancaria.
     * Valida la solicitud, verifica si la cuenta origen contiene saldo
     * disponible para realizar la transferancia.
     *
     * @param transferRequest DTO con los datos necesarios para realizar la transferencia
     * @return Objeto DTO con la respuesta del resultado de la operación
     */
    ApiResponseDTO processBankTransfer(TransferRequestDTO transferRequest);

    /**
     * Retorna todas las cuentas bancarias segun un estatus indicado.
     *
     * @param status estatus de cuentas a buscar
     * @return Lista de cuentas bancarias que coinciden con los criterios de búsqueda
     */
    List<Account> getAccountsByStatus(String status);

    /**
     * Retorna todas las comisiones cobradas a una cuenta bancaria en un pediodo
     * de tiempo establecido.
     *
     * @param id Identificador único de la cuenta
     * @param startDate fecha inicial del período de consulta
     * @param endDate fecha final del período de consulta
     * @return Lista de saldos diarios que coinciden con los criterios de búsqueda
     */
    List<BankFee> getBankFees(String id, LocalDate startDate, LocalDate endDate);
}
