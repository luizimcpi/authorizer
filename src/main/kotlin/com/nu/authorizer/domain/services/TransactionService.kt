package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.common.constants.Constants.DOUBLED_TRANSACTION
import com.nu.authorizer.domain.common.constants.Constants.HIGH_FREQUENCY_SMALL_INTERVAL
import com.nu.authorizer.domain.common.constants.Constants.VIOLATION_CARD_NOT_ACTIVE
import com.nu.authorizer.domain.common.constants.Constants.VIOLATION_INSUFICIENT_LIMIT
import com.nu.authorizer.domain.common.utils.LocalDateTimeUtils.getDifferenceInMinutes
import com.nu.authorizer.domain.exception.AccountNotFoundException
import com.nu.authorizer.domain.model.entities.Account
import com.nu.authorizer.domain.model.requests.TransactionRequest
import com.nu.authorizer.domain.model.responses.AccountResponse
import com.nu.authorizer.domain.repositories.AccountRepository
import com.nu.authorizer.domain.repositories.TransactionRepository

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
        if (existsSimilarTransactionsOnTwoMinuteInterval()) {
            violations.add(DOUBLED_TRANSACTION)
        }
    }

    private fun existsSimilarTransactionsOnTwoMinuteInterval(): Boolean {
        val lastTransactions = repository.getLastTransactions().take(3)
        val sameAmount = lastTransactions.map { it.amount }.toList().stream().distinct().count() <= 1
        val sameMerchant = lastTransactions.map { it.merchant }.toList().stream().distinct().count() <= 1

        if (sameAmount && sameMerchant) {
            return existsTransactionsOnTwoMinuteInterval(true)
        }
        return false
    }

    private fun existsTransactionsOnTwoMinuteInterval(similarTransactions: Boolean = false): Boolean {
        val lastTransactions = if (similarTransactions) {
            repository.getLastTransactions().take(3)
        } else {
            repository.getLastTransactions()
        }
        val allTransactionsInTheSameDay =
            if (similarTransactions) lastTransactions.size == 3
            else lastTransactions.size == 4 && lastTransactions.map { it.time.toLocalDate() }.toList()
                .stream()
                .distinct()
                .count() <= 1

        val allTransactionsInTheSameHour = lastTransactions.map { it.time.hour }
            .toList()
            .stream()
            .distinct()
            .count() <= 1

        if (allTransactionsInTheSameDay && allTransactionsInTheSameHour) {
            val lastDateTime = lastTransactions.map { it.time }.toList().first()
            val firstDateTime = lastTransactions.map { it.time }.toList().last()
            if (getDifferenceInMinutes(firstDateTime, lastDateTime) <= 2) return true
        }

        return false
    }
}
