package com.nu.authorizer.domain.services.violation

import com.nu.authorizer.domain.model.entities.Account
import com.nu.authorizer.domain.model.entities.Transaction
import com.nu.authorizer.domain.model.requests.TransactionRequest

open class AmountViolationService : Violation {
    override fun check(
        transactionRequest: TransactionRequest?,
        account: Account?,
        violations: MutableList<String>,
        lastTransactions: List<Transaction>?,
        violationDescription: String
    ) {
        if (transactionRequest!!.transaction.amount > account!!.availableLimit)
            violations.add(violationDescription)
    }
}
