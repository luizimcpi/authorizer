package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.common.constants.Constants.DOUBLED_TRANSACTION
import com.nu.authorizer.domain.common.constants.Constants.FIRST_TRANSACTION_HIGH_AMOUNT
import com.nu.authorizer.domain.common.constants.Constants.HIGH_FREQUENCY_SMALL_INTERVAL
import com.nu.authorizer.domain.common.constants.Constants.VIOLATION_CARD_NOT_ACTIVE
import com.nu.authorizer.domain.common.constants.Constants.VIOLATION_INSUFICIENT_LIMIT
import com.nu.authorizer.domain.exception.AccountNotFoundException
import com.nu.authorizer.domain.model.entities.Account
import com.nu.authorizer.domain.model.entities.Transaction
import com.nu.authorizer.domain.model.requests.TransactionRequest
import com.nu.authorizer.domain.model.responses.AccountResponse
import com.nu.authorizer.domain.repositories.AccountRepository
import com.nu.authorizer.domain.repositories.TransactionRepository
import com.nu.authorizer.domain.services.violation.ActiveCardViolationService
import com.nu.authorizer.domain.services.violation.AmountViolationService
import com.nu.authorizer.domain.services.violation.FirstTransactionAmountViolationService
import com.nu.authorizer.domain.services.violation.SimilarTransactionsViolationService
import com.nu.authorizer.domain.services.violation.TransactionsIntervalViolationService
import com.nu.authorizer.domain.services.violation.Violation
import java.util.HashMap

class TransactionService(
    private val accountRepository: AccountRepository,
    private val repository: TransactionRepository
) : GenericService<TransactionRequest> {

    var violationsServiceMap: Map<String, Violation> = HashMap()

    override fun process(transactionRequest: TransactionRequest): AccountResponse {

        val account = accountRepository.getAccount()
        val violations = mutableListOf<String>()
        if (account != null) {
            val lastTransactions = repository.getLastTransactions()
            checkViolations(transactionRequest, account, violations, lastTransactions)
            if (violations.isEmpty()) {
                val debitAccount = account.copy(availableLimit = account.debit(transactionRequest.transaction.amount))
                accountRepository.update(debitAccount, 0)
                repository.save(transactionRequest.transaction)
                return AccountResponse(account = debitAccount, violations = violations)
            }
            return AccountResponse(account = account, violations = violations)
        }

        throw AccountNotFoundException("Account Not Found")
    }

    private fun checkViolations(
        transactionRequest: TransactionRequest,
        account: Account,
        violations: MutableList<String>,
        lastTransactions: List<Transaction>
    ) {
        violationsServiceMap = mapOf(
            Pair(VIOLATION_INSUFICIENT_LIMIT, AmountViolationService()),
            Pair(VIOLATION_CARD_NOT_ACTIVE, ActiveCardViolationService()),
            Pair(HIGH_FREQUENCY_SMALL_INTERVAL, TransactionsIntervalViolationService()),
            Pair(DOUBLED_TRANSACTION, SimilarTransactionsViolationService()),
            Pair(FIRST_TRANSACTION_HIGH_AMOUNT, FirstTransactionAmountViolationService())
        )

        violationsServiceMap.forEach { (key, rules) ->
            rules.check(
                transactionRequest = transactionRequest,
                account = account,
                violations = violations,
                lastTransactions = lastTransactions,
                violationDescription = key
            )
        }
    }
}
