package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.common.constants.Constants.DOUBLED_TRANSACTION
import com.nu.authorizer.domain.common.constants.Constants.HIGH_FREQUENCY_SMALL_INTERVAL
import com.nu.authorizer.domain.common.constants.Constants.VIOLATION_CARD_NOT_ACTIVE
import com.nu.authorizer.domain.common.constants.Constants.VIOLATION_INSUFICIENT_LIMIT
import com.nu.authorizer.domain.exception.AccountNotFoundException
import com.nu.authorizer.domain.model.entities.Account
import com.nu.authorizer.domain.model.requests.TransactionRequest
import com.nu.authorizer.domain.model.responses.AccountResponse
import com.nu.authorizer.domain.repositories.AccountRepository
import com.nu.authorizer.domain.repositories.TransactionRepository
import com.nu.authorizer.domain.services.TransactionIntervalRules.existsSimilarTransactionsOnTwoMinuteInterval
import com.nu.authorizer.domain.services.TransactionIntervalRules.existsTransactionsOnTwoMinuteInterval

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
        val lastTransactions = repository.getLastTransactions()

        if (transactionRequest.transaction.amount > account.availableLimit) {
            violations.add(VIOLATION_INSUFICIENT_LIMIT)
        }
        if (!account.activeCard) {
            violations.add(VIOLATION_CARD_NOT_ACTIVE)
        }
        if (existsTransactionsOnTwoMinuteInterval(lastTransactions)) {
            violations.add(HIGH_FREQUENCY_SMALL_INTERVAL)
        }
        if (existsSimilarTransactionsOnTwoMinuteInterval(transactions = lastTransactions)) {
            violations.add(DOUBLED_TRANSACTION)
        }
    }
}
