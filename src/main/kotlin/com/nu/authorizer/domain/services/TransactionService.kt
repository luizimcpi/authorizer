package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.model.entities.Account
import com.nu.authorizer.domain.model.requests.TransactionRequest
import com.nu.authorizer.domain.model.responses.AccountResponse
import com.nu.authorizer.domain.repositories.TransactionRepository

class TransactionService(private val repository: TransactionRepository) : GenericService<TransactionRequest, AccountResponse> {

    override fun create(transactionRequest: TransactionRequest): AccountResponse {
        return AccountResponse(account = Account(true, 100))
    }
}
