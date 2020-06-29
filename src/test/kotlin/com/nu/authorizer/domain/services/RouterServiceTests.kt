package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.common.config.JacksonConfig
import com.nu.authorizer.domain.common.config.ObjectMapperConfig
import com.nu.authorizer.domain.model.requests.AccountRequest
import com.nu.authorizer.domain.model.requests.TransactionRequest
import com.nu.authorizer.resources.repositories.AccountInMemoryRepository
import com.nu.authorizer.resources.repositories.TransactionInMemoryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RouterServiceTests {

    private lateinit var accountService: AccountService
    private lateinit var accountRepository: AccountInMemoryRepository
    private lateinit var routerAccountService: RouterService<AccountRequest>

    private lateinit var transactionService: TransactionService
    private lateinit var transactionRepository: TransactionInMemoryRepository
    private lateinit var routerTransactionService: RouterService<TransactionRequest>

    @BeforeEach
    fun setUp() {
        JacksonConfig.configure(ObjectMapperConfig.jsonObjectMapper)
        accountRepository = AccountInMemoryRepository()
        accountService = AccountService(accountRepository)
        routerAccountService = RouterService(accountService)

        transactionRepository = TransactionInMemoryRepository()
        transactionService = TransactionService(accountService, transactionRepository)
        routerTransactionService = RouterService(transactionService)
    }

    @Test
    fun `when receive valid string of accountRequest should return an instance of accountResponse`() {
        val validAccountRequestLine =
            """{ "account": { "activeCard": true, "availableLimit": 100 } }"""
        val response = routerAccountService.getResponse(validAccountRequestLine)

        assertEquals(true, response.account.activeCard)
        assertEquals(100, response.account.availableLimit)
    }

    @Test
    fun `when receive valid string of transactionRequest should return an instance of accountResponse`() {
        val validTransactionRequestLine =
            """{ "transaction": { "merchant": "Burger King", "amount": 20, "time": "2019-02-13T10:00:00.000Z" } }"""
        val response = routerTransactionService.getResponse(validTransactionRequestLine)

        assertEquals(true, response.account.activeCard)
        assertEquals(100, response.account.availableLimit)
    }
}
