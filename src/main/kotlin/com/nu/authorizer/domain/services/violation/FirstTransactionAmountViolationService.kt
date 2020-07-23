package com.nu.authorizer.domain.services.violation

import com.nu.authorizer.domain.model.entities.Account
import com.nu.authorizer.domain.model.entities.Transaction
import com.nu.authorizer.domain.model.requests.TransactionRequest

class FirstTransactionAmountViolationService : Violation {
    override fun check(
        transactionRequest: TransactionRequest?,
        account: Account?,
        violations: MutableList<String>,
        lastTransactions: List<Transaction>?,
        violationDescription: String
    ) {
        if (lastTransactions!!.isEmpty() && breakPercentageRule(transactionRequest, account))
            violations.add(violationDescription)
    }

    private fun breakPercentageRule(
        transactionRequest: TransactionRequest?,
        account: Account?
    ) = transactionRequest!!.transaction.amount > account!!.availableLimit * 0.9
}
