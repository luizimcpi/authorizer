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
    private val lineSeparator = System.getProperty("line.separator")

    @BeforeEach
    private fun setUp() {
        JacksonConfig.configure(ObjectMapperConfig.jsonObjectMapper)
        accountRepository = AccountInMemoryRepository()
        accountService = AccountService(accountRepository)
        routerAccountService = RouterService(accountService)
        transactionRepository = TransactionInMemoryRepository()
        transactionService = TransactionService(accountRepository, transactionRepository)
        routerTransactionService = RouterService(transactionService)
        presenter = EventStreamPresenter(routerAccountService, routerTransactionService)
        outContent = ByteArrayOutputStream()
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
        assertEquals("$expected$lineSeparator", outContent.toString())
    }

    @Test
    fun `when receive valid string account and transaction request with amount zero should return an account string response in terminal`() {
        val validAccountRequest =
            """{ "account": { "activeCard": true, "availableLimit": 1 } }"""

        val validTransactionRequest =
            """{ "transaction": { "merchant": "Burger King", "amount": 0, "time": "2019-02-13T10:00:00.000Z" } }"""

        val expectedAccountResponse =
            """{"account":{"activeCard":true,"availableLimit":1},"violations":[]}"""

        val expectedTransactionResponse =
            """{"account":{"activeCard":true,"availableLimit":1},"violations":[]}"""

        System.setOut(PrintStream(outContent))

        val e = Exception()
        presenter.printLines(listOf(validAccountRequest, validTransactionRequest))
        assertDoesNotThrow { e }

        val expected = "$expectedAccountResponse$lineSeparator$expectedTransactionResponse$lineSeparator"
        assertEquals(expected, outContent.toString())
    }

    @Test
    fun `when receive valid string transaction request should return an account string response in terminal`() {
        val validAccountRequest =
            """{ "account": { "activeCard": true, "availableLimit": 100 } }"""

        val validRequest =
            """{ "transaction": { "merchant": "Burger King", "amount": 20, "time": "2019-02-13T10:00:00.000Z" } }"""

        val expectedCreated =
            """{"account":{"activeCard":true,"availableLimit":100},"violations":[]}"""

        val expectedDebit =
            """{"account":{"activeCard":true,"availableLimit":80},"violations":[]}"""

        System.setOut(PrintStream(outContent))

        val e = Exception()
        presenter.printLines(listOf(validAccountRequest, validRequest))
        assertDoesNotThrow { e }
        val expected = "$expectedCreated$lineSeparator$expectedDebit$lineSeparator"
        assertEquals(expected, outContent.toString())
    }
}
