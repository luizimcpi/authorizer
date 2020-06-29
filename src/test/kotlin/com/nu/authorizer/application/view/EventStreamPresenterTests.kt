package com.nu.authorizer.application.view

import com.nu.authorizer.domain.common.config.JacksonConfig
import com.nu.authorizer.domain.common.config.ObjectMapperConfig
import com.nu.authorizer.domain.model.requests.AccountRequest
import com.nu.authorizer.domain.model.requests.TransactionRequest
import com.nu.authorizer.domain.services.AccountService
import com.nu.authorizer.domain.services.RouterService
import com.nu.authorizer.domain.services.TransactionService
import com.nu.authorizer.resources.repositories.AccountInMemoryRepository
import com.nu.authorizer.resources.repositories.TransactionInMemoryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class EventStreamPresenterTests {

    private lateinit var accountRepository: AccountInMemoryRepository
    private lateinit var accountService: AccountService
    private lateinit var routerAccountService: RouterService<AccountRequest>
    private lateinit var transactionRepository: TransactionInMemoryRepository
    private lateinit var transactionService: TransactionService
    private lateinit var routerTransactionService: RouterService<TransactionRequest>
    private lateinit var presenter: EventStreamPresenter
    private lateinit var outContent: ByteArrayOutputStream

    @BeforeEach
    private fun setUp() {
        JacksonConfig.configure(ObjectMapperConfig.jsonObjectMapper)
        accountRepository = AccountInMemoryRepository()
        accountService = AccountService(accountRepository)
        routerAccountService = RouterService(accountService)
        transactionRepository = TransactionInMemoryRepository()
        transactionService = TransactionService(accountService, transactionRepository)
        routerTransactionService = RouterService(transactionService)
        presenter = EventStreamPresenter(routerAccountService, routerTransactionService)
        outContent = ByteArrayOutputStream()
    }

    @Test
    fun `when receive invalid string request should deal with exception and return friendly message in terminal `() {
        val invalidRequest =
            """{ "invalidPayload": { "activeCard": true, "availableLimit": 100 } }"""

        var expected = "{\"errorMessage\":\"Invalid Request\"}"
        System.setOut(PrintStream(outContent))

        val e = Exception()
        presenter.printLines(listOf(invalidRequest))
        assertDoesNotThrow { e }
        assertEquals(expected + System.getProperty("line.separator"), outContent.toString())
    }

    @Test
    fun `when receive invalid string account request should deal with exception and return friendly message in terminal `() {
        val invalidRequest =
            """{ "invalidPayload": { "account": true, "availableLimit": 100 } }"""

        val expected = "{\"errorMessage\":\"Class type conversion error, check your json request\"}"
        System.setOut(PrintStream(outContent))

        val e = Exception()
        presenter.printLines(listOf(invalidRequest))
        assertDoesNotThrow { e }
        assertEquals(expected + System.getProperty("line.separator"), outContent.toString())
    }

    @Test
    fun `when receive invalid string transaction request should deal with exception and return friendly message in terminal `() {
        val invalidRequest =
            """{ "invalidPayload": { "transaction": "Burger King", "amount": 20, "time": "2019-02-13T10:00:00.000Z" } }"""

        val expected = "{\"errorMessage\":\"Class type conversion error, check your json request\"}"
        System.setOut(PrintStream(outContent))

        val e = Exception()
        presenter.printLines(listOf(invalidRequest))
        assertDoesNotThrow { e }
        assertEquals(expected + System.getProperty("line.separator"), outContent.toString())
    }

    @Test
    fun `when receive valid string account request should return an account string response in terminal`() {
        val validRequest =
            """{ "account": { "activeCard": true, "availableLimit": 100 } }"""

        val expected =
            """{"account":{"activeCard":true,"availableLimit":100},"violations":[]}"""

        System.setOut(PrintStream(outContent))

        val e = Exception()
        presenter.printLines(listOf(validRequest))
        assertDoesNotThrow { e }
        assertEquals(expected + System.getProperty("line.separator"), outContent.toString())
    }

    @Test
    fun `when receive valid string transaction request should return an account string response in terminal`() {
        val validRequest =
            """{ "transaction": { "merchant": "Burger King", "amount": 20, "time": "2019-02-13T10:00:00.000Z" } }"""

        val expected =
            """{"account":{"activeCard":true,"availableLimit":200},"violations":[]}"""

        System.setOut(PrintStream(outContent))

        val e = Exception()
        presenter.printLines(listOf(validRequest))
        assertDoesNotThrow { e }
        assertEquals(expected + System.getProperty("line.separator"), outContent.toString())
    }
}
