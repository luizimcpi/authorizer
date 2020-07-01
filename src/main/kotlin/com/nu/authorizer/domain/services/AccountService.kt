package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.model.requests.AccountRequest
import com.nu.authorizer.domain.model.responses.AccountResponse
import com.nu.authorizer.domain.repositories.AccountRepository

class AccountService(private val repository: AccountRepository) : GenericService<AccountRequest> {

    override fun process(accountRequest: AccountRequest): AccountResponse {
        val registeredAccount = repository.getAccount()
        if (registeredAccount != null) {
            return AccountResponse(registeredAccount, listOf("account-already-initialized"))
        }
        repository.save(accountRequest.account)
        return AccountResponse(account = accountRequest.account)
    }
}
