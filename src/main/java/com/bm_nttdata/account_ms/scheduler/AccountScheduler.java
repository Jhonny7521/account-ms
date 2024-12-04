package com.bm_nttdata.account_ms.scheduler;

import com.bm_nttdata.account_ms.entity.Account;
import com.bm_nttdata.account_ms.entity.DailyBalance;
import com.bm_nttdata.account_ms.enums.AccountStatusEnum;
import com.bm_nttdata.account_ms.enums.AccountTypeEnum;
import com.bm_nttdata.account_ms.exception.ServiceException;
import com.bm_nttdata.account_ms.repository.AccountRepository;
import com.bm_nttdata.account_ms.repository.DailyBalanceRepository;
import com.bm_nttdata.account_ms.util.Constants;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Programador de tareas para la gestión de cuentas bancarias VIP.
 * Esta clase maneja las operaciones programadas relacionadas con el registro de saldos diarios
 * y la validación de promedios mensuales para cuentas de ahorro VIP.
 */
@Component
@Slf4j
public class AccountScheduler {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DailyBalanceRepository dailyBalanceRepository;

    /**
     * Registra los saldos diarios de todas las cuentas de ahorro VIP.
     * Se ejecuta automáticamente todos los días a medianoche.
     * Obtiene todas las cuentas VIP y registra sus saldos actuales.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void recordDailyBalances() {

        try {
            LocalDate currentDay = LocalDate.now();
            List<Account> accounts =
                    accountRepository.findAllByAccountType(AccountTypeEnum.SAVINGS_VIP);

            for (Account account : accounts) {
                try {
                    recordDailyBalance(account, currentDay);

                } catch (Exception e) {
                    log.error("Error processing the daily balance for the account {}: {}",
                            account.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error when processing daily balances: {}", e.getMessage());
        }
    }

    /**
     * Registra el saldo diario de una cuenta específica.
     *
     * @param account La cuenta cuyo saldo se va a registrar
     * @param date La fecha del registro
     */
    private void recordDailyBalance(Account account, LocalDate date) {

        try {
            DailyBalance dailyBalance = DailyBalance.builder()
                    .accountId(account.getId())
                    .date(date)
                    .balance(account.getBalance())
                    .build();
            dailyBalanceRepository.save(dailyBalance);
        } catch (Exception e) {
            log.error("Database error while saving the daily balance: {}", e.getMessage());
            throw new RuntimeException("Error saving daily balance", e);
        }
    }

    /**
     * Valida los promedios diarios mensuales de todas las cuentas VIP.
     * Se ejecuta automáticamente el primer día de cada mes.
     * Verifica si las cuentas VIP mantienen el saldo promedio mínimo requerido.
     */
    @Scheduled(cron = "0 0 1 1 * *") // Primer día de cada mes
    public void validateMonthlyAverages() {
        try {
            LocalDate previousMonth = LocalDate.now().minusMonths(1);
            YearMonth month = YearMonth.from(previousMonth);
            List<Account> accounts =
                    accountRepository.findAllByAccountType(AccountTypeEnum.SAVINGS_VIP);

            for (Account account : accounts) {
                try {
                    validateMonthlyAverage(account, month);
                } catch (Exception e) {
                    log.error("Error validating the monthly average for the account {}: {}",
                            account.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error when validating monthly averages: {}", e.getMessage());
        }
    }

    /**
     * Valida el promedio mensual de una cuenta específica.
     * Si el promedio está por debajo del mínimo requerido, la cuenta se bloquea.
     *
     * @param account La cuenta a validar
     * @param month El mes para el cual se calcula el promedio
     */
    private void validateMonthlyAverage(Account account, YearMonth month) {

        try {
            BigDecimal average = calculateMonthlyAverageBalance(account.getId(), month);

            if (average.compareTo(Constants.VIP_MINIMUM_DAILY_AVERAGE) < 0) {

                account.setStatus(AccountStatusEnum.BLOCKED.getValue());
                accountRepository.save(account);
            }
        } catch (DataAccessException e) {
            log.error("Database error when validating account {}: {}",
                    account.getId(), e.getMessage());
            throw new RuntimeException("Error validating monthly average", e);

        } catch (ArithmeticException e) {
            log.error("Calculation error for the account {}: {}",
                    account.getId(), e.getMessage());
            throw new RuntimeException("Error in average calculation", e);
        }
    }

    /**
     * Calcula el promedio mensual de saldo para una cuenta específica.
     *
     * @param accountId El ID de la cuenta para calcular el promedio
     * @param month El mes para el cual se calcula el promedio
     * @return El promedio mensual de saldo con dos decimales, redondeado hacia arriba
     *         Retorna cero si no hay registros de saldo para el mes
     */
    public BigDecimal calculateMonthlyAverageBalance(String accountId, YearMonth month) {

        try {
            LocalDate startDate = month.atDay(1);
            LocalDate endDate = month.atEndOfMonth();

            List<DailyBalance> dailyBalances = dailyBalanceRepository
                    .findByAccountIdAndDateBetween(accountId, startDate, endDate);

            if (dailyBalances.isEmpty()) {
                return BigDecimal.ZERO;
            }

            BigDecimal totalBalance = dailyBalances.stream()
                    .map(DailyBalance::getBalance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return totalBalance.divide(
                    BigDecimal.valueOf(dailyBalances.size()), 2, RoundingMode.HALF_UP);

        } catch (DataAccessException e) {
            log.error("Error obtaining daily balances for account {}: {}",
                    accountId, e.getMessage());
            throw new RuntimeException("Error when calculating monthly average", e);

        } catch (ArithmeticException e) {
            log.error("Error in calculation for account {}: {}", accountId, e.getMessage());
            throw new RuntimeException("Error in average division", e);
        }
    }
}
