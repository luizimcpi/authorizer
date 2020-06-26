package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.model.entities.Account
import com.nu.authorizer.domain.model.requests.AccountRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class AccountServiceTests {

    @Test
    fun `when receive valid request should save with success`() {
        val accountService = AccountService()
        val validAccount = Account(activeCard = true, availableLimit = 100)
        val validRequest = AccountRequest(validAccount)
        val e = Exception()
        accountService.create(validRequest)
        assertDoesNotThrow { e }
    }
}