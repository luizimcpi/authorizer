package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.common.config.JacksonConfig
import com.nu.authorizer.domain.common.config.ObjectMapperConfig
import com.nu.authorizer.domain.model.responses.AccountResponse
import com.nu.authorizer.resources.repositories.AccountInMemoryRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RouterServiceTests {

    private lateinit var accountService: AccountService
    private lateinit var accountRepository: AccountInMemoryRepository
    private lateinit var routerService: RouterService

    @BeforeEach
    fun setUp() {
        JacksonConfig.configure(ObjectMapperConfig.jsonObjectMapper)
        accountRepository = AccountInMemoryRepository()
        accountService = AccountService(accountRepository)
        routerService = RouterService(accountService)
    }

    @Test
    fun `when receive valid string of accountRequest should return an instance of accountResponse`() {
        val validAccountRequestLine =
            """{ "account": { "activeCard": true, "availableLimit": 100 } }"""
        val response = routerService.getResponse(validAccountRequestLine)
        assertTrue(response is AccountResponse)
    }
}
