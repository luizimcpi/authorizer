package com.nu.authorizer.domain.services.violation

import com.nu.authorizer.domain.model.entities.Account
import com.nu.authorizer.domain.model.entities.Transaction
import com.nu.authorizer.domain.model.requests.TransactionRequest
import com.nu.authorizer.domain.services.TransactionIntervalRules.existsTransactionsOnTwoMinuteInterval

class TransactionsIntervalViolationService : Violation {
    override fun check(
        transactionRequest: TransactionRequest?,
        account: Account?,
        violations: MutableList<String>,
        lastTransactions: List<Transaction>?,
        violationDescription: String
    ) {
        if (existsTransactionsOnTwoMinuteInterval(lastTransactions!!))
            violations.add(violationDescription)
    }
}
