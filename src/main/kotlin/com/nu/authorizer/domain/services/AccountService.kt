package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.model.requests.AccountRequest
import com.nu.authorizer.domain.model.responses.AccountResponse

class AccountService {

    fun create(accountRequest: AccountRequest): AccountResponse {
        println("Chegou no service $accountRequest")
        return AccountResponse(accountRequest.account, listOf("account-already-initialized"))
    }
}