package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.model.entities.Account
import com.nu.authorizer.domain.model.requests.AccountRequest
import com.nu.authorizer.resources.repositories.AccountInMemoryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class AccountServiceTests {

    private lateinit var accountService: AccountService
    private lateinit var accountRepository: AccountInMemoryRepository

    @BeforeEach
    fun setUp(){
        accountRepository = AccountInMemoryRepository()
        accountService = AccountService(accountRepository)
    }

    @Test
    fun `when receive valid accountRequest should not throw any exception`() {
        val validAccount = Account(activeCard = true, availableLimit = 100)
        val validRequest = AccountRequest(validAccount)
        val e = Exception()
        accountService.create(validRequest)
        assertDoesNotThrow { e }
    }

    @Test
    fun `when receive valid  accountRequest should create with success`() {
        val validAccount = Account(activeCard = true, availableLimit = 100)
        val validRequest = AccountRequest(validAccount)
        val validResponse = accountService.create(validRequest)
        assertTrue(validResponse.violations.isEmpty())
        assertEquals(true, validResponse.account.activeCard)
        assertEquals(100, validResponse.account.availableLimit)
    }

    @Test
    fun `when receive an accountRequest with account that already registered should return response with violations`() {
        val validAccount = Account(activeCard = true, availableLimit = 100)
        val validRequest = AccountRequest(validAccount)
        val repeatedAccount = Account(activeCard = true, availableLimit = 300)
        val repeatedAccountRequest = AccountRequest(repeatedAccount)
        val expectedViolationArray = listOf("account-already-initialized")

        val validResponse = accountService.create(validRequest)
        val violationResponse = accountService.create(repeatedAccountRequest)

        assertTrue(validResponse.violations.isEmpty())
        assertEquals(expectedViolationArray.first(), violationResponse.violations.first())
        assertEquals(validResponse.account.activeCard, violationResponse.account.activeCard)
        assertEquals(validResponse.account.availableLimit, violationResponse.account.availableLimit)
    }
}