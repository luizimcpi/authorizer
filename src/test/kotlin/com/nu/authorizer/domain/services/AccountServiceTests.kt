package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.model.entities.Account
import com.nu.authorizer.domain.model.requests.AccountRequest
import com.nu.authorizer.resources.repositories.AccountInMemoryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class AccountServiceTests {

    @Test
    fun `when receive valid request should save with success`() {
        val accountRepository = AccountInMemoryRepository()
        val accountService = AccountService(accountRepository)
        val validAccount = Account(activeCard = true, availableLimit = 100)
        val validRequest = AccountRequest(validAccount)
        val e = Exception()
        accountService.create(validRequest)
        assertDoesNotThrow { e }
    }

    @Test
    fun `when receive an account that already registered should return response with violations`() {
        val accountRepository = AccountInMemoryRepository()
        val accountService = AccountService(accountRepository)
        val validAccount = Account(activeCard = true, availableLimit = 100)
        val validRequest = AccountRequest(validAccount)
        val repeatedAccount = Account(activeCard = true, availableLimit = 300)
        val repeatedAccountRequest = AccountRequest(repeatedAccount)
        val expectedViolationArray = listOf("account-already-initialized")

        val validResponse = accountService.create(validRequest)
        val violationResponse = accountService.create(repeatedAccountRequest)

        assertTrue(validResponse.violations.isEmpty())
        assertEquals(expectedViolationArray.first(), violationResponse.violations.first())
        assertEquals(100, violationResponse.account.availableLimit)
    }
}