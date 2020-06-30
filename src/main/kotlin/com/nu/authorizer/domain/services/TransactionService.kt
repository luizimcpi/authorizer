package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.common.constants.Constants.HIGH_FREQUENCY_SMALL_INTERVAL
import com.nu.authorizer.domain.common.constants.Constants.VIOLATION_CARD_NOT_ACTIVE
import com.nu.authorizer.domain.common.constants.Constants.VIOLATION_INSUFICIENT_LIMIT
import com.nu.authorizer.domain.exception.AccountNotFoundException
import com.nu.authorizer.domain.model.entities.Account
import com.nu.authorizer.domain.model.requests.TransactionRequest
import com.nu.authorizer.domain.model.responses.AccountResponse
import com.nu.authorizer.domain.repositories.AccountRepository
import com.nu.authorizer.domain.repositories.TransactionRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class TransactionService(
    private val accountRepository: AccountRepository,
    private val repository: TransactionRepository
) : GenericService<TransactionRequest> {

    override fun process(transactionRequest: TransactionRequest): AccountResponse {
        repository.save(transactionRequest.transaction)
        val account = accountRepository.getAccount()
        val violations = mutableListOf<String>()
        if (account != null) {
            checkViolations(transactionRequest, account, violations)
            if (violations.isEmpty()) {
                val debitAccount = account.copy(availableLimit = account.debit(transactionRequest.transaction.amount))
                accountRepository.update(debitAccount, 0)
                return AccountResponse(account = debitAccount, violations = violations)
            }
            return AccountResponse(account = account, violations = violations)
        }

        throw AccountNotFoundException("Account Not Found")
    }

    private fun checkViolations(
        transactionRequest: TransactionRequest,
        account: Account,
        violations: MutableList<String>
    ) {
        if (transactionRequest.transaction.amount > account.availableLimit) {
            violations.add(VIOLATION_INSUFICIENT_LIMIT)
        }
        if (!account.activeCard) {
            violations.add(VIOLATION_CARD_NOT_ACTIVE)
        }
        if (existsTransactionsOnTwoMinuteInterval()) {
            violations.add(HIGH_FREQUENCY_SMALL_INTERVAL)
        }
    }

    private fun existsTransactionsOnTwoMinuteInterval(): Boolean {

        val convertedToDate = repository.getLastTransactions()
            .map { it.time.toLocalDate() }
            .toList()

        var lastDay = LocalDate.MAX
        var cont = 0

        convertedToDate.forEachIndexed { i, value ->
            if (i == 0) {
                lastDay = value
            }
            if (lastDay == value) {
                cont++
            }
            lastDay = value
        }

        if (cont == 3) {
            return true
//            var lastDay = LocalDateTime.MAX
//            difference =  repository.getLastTransactions().map { checkIntervalViolation(it.time, lastDay) }.sum()
        }
        return false
//        return difference > 2
    }

    private fun checkIntervalViolation(actualDate: LocalDateTime, previousDate: LocalDateTime): Long {
        if (actualDate.toLocalDate() == previousDate.toLocalDate()) {
            return ChronoUnit.MINUTES.between(previousDate, actualDate)
        }
        return 0
    }
}
