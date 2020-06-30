package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.common.constants.Constants.VIOLATION_CARD_NOT_ACTIVE
import com.nu.authorizer.domain.common.constants.Constants.VIOLATION_INSUFICIENT_LIMIT
import com.nu.authorizer.domain.model.entities.Account
import com.nu.authorizer.domain.model.entities.Transaction
import com.nu.authorizer.domain.model.requests.TransactionRequest
import com.nu.authorizer.resources.repositories.AccountInMemoryRepository
import com.nu.authorizer.resources.repositories.TransactionInMemoryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class TransactionServiceTests {

    private lateinit var transactionService: TransactionService
    private lateinit var accountRepository: AccountInMemoryRepository
    private lateinit var transactionRepository: TransactionInMemoryRepository

    @BeforeEach
    fun setUp() {
        transactionRepository = TransactionInMemoryRepository()
        accountRepository = AccountInMemoryRepository()
        transactionService = TransactionService(accountRepository, transactionRepository)
    }

    @Test
    fun `when account exists and receive valid transactionRequest should debit availableLimit from account`() {
        val validAccount = Account(activeCard = true, availableLimit = 100)
        accountRepository.save(validAccount)
        val transaction = Transaction(merchant = "Burger King", amount = 20L, time = LocalDateTime.now())
        val validTransactionRequest = TransactionRequest(transaction)
        val response = transactionService.process(validTransactionRequest)
        assertTrue(response.violations.isEmpty())
        assertEquals(true, response.account.activeCard)
        assertEquals(80, response.account.availableLimit)
    }

    @Test
    fun `when account exists and receive valid transactionRequest that pass availble limit should return violations`() {
        val validAccount = Account(activeCard = true, availableLimit = 100)
        accountRepository.save(validAccount)
        val transaction = Transaction(merchant = "Burger King", amount = 120L, time = LocalDateTime.now())
        val validTransactionRequest = TransactionRequest(transaction)
        val response = transactionService.process(validTransactionRequest)
        assertEquals(VIOLATION_INSUFICIENT_LIMIT, response.violations.first())
        assertEquals(true, response.account.activeCard)
        assertEquals(100, response.account.availableLimit)
    }

    @Test
    fun `when account exists with inactive card and receive valid transactionRequest should return violations`() {
        val validAccount = Account(activeCard = false, availableLimit = 100)
        accountRepository.save(validAccount)
        val transaction = Transaction(merchant = "Burger King", amount = 20L, time = LocalDateTime.now())
        val validTransactionRequest = TransactionRequest(transaction)
        val response = transactionService.process(validTransactionRequest)
        assertEquals(VIOLATION_CARD_NOT_ACTIVE, response.violations.first())
        assertEquals(false, response.account.activeCard)
        assertEquals(100, response.account.availableLimit)
    }

    @Test
    fun `when account exists with inactive card and receive valid transactionRequest that pass availble limit should return violations`() {
        val validAccount = Account(activeCard = false, availableLimit = 100)
        accountRepository.save(validAccount)
        val transaction = Transaction(merchant = "Burger King", amount = 120L, time = LocalDateTime.now())
        val validTransactionRequest = TransactionRequest(transaction)
        val response = transactionService.process(validTransactionRequest)
        assertEquals(VIOLATION_INSUFICIENT_LIMIT, response.violations.first())
        assertEquals(VIOLATION_CARD_NOT_ACTIVE, response.violations[1])
        assertEquals(false, response.account.activeCard)
        assertEquals(100, response.account.availableLimit)
    }
}
