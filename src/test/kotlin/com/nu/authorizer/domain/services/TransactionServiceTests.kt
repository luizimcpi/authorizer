package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.common.constants.Constants.DOUBLED_TRANSACTION
import com.nu.authorizer.domain.common.constants.Constants.HIGH_FREQUENCY_SMALL_INTERVAL
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
        assertTrue(response.violations.size == 1)
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
        assertTrue(response.violations.size == 1)
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
        assertTrue(response.violations.size == 2)
        assertEquals(VIOLATION_INSUFICIENT_LIMIT, response.violations.first())
        assertEquals(VIOLATION_CARD_NOT_ACTIVE, response.violations[1])
        assertEquals(false, response.account.activeCard)
        assertEquals(100, response.account.availableLimit)
    }

    @Test
    fun `when account exists with active card and good availbleLimit and receive more than three valid transactionRequests in the same day that violates frequency in small interval should return violations`() {
        val validAccount = Account(activeCard = true, availableLimit = 10000)
        accountRepository.save(validAccount)

        val transactionBurgerKing = Transaction(merchant = "Burger King", amount = 20L, time = LocalDateTime.of(2020, 6, 1, 14, 42, 45))
        val requestBurgerKing = TransactionRequest(transactionBurgerKing)

        val transactionAmericanas =
            Transaction(merchant = "Lojas Americanas", amount = 120L, time = LocalDateTime.of(2020, 6, 1, 14, 41, 52))
        val requestAmericanas = TransactionRequest(transactionAmericanas)
        transactionService.process(requestAmericanas)

        val transactionSubmarino =
            Transaction(merchant = "Submarino.com", amount = 2200L, time = LocalDateTime.of(2020, 6, 1, 14, 41, 56))
        val requestSubmarino = TransactionRequest(transactionSubmarino)
        transactionService.process(requestSubmarino)

        val transactionRenner =
            Transaction(merchant = "Lojas Renner", amount = 370L, time = LocalDateTime.of(2020, 6, 1, 14, 41, 59))
        val requestRenner = TransactionRequest(transactionRenner)
        transactionService.process(requestRenner)

        val response = transactionService.process(requestBurgerKing)
        assertTrue(response.violations.size == 1)
        assertEquals(HIGH_FREQUENCY_SMALL_INTERVAL, response.violations.first())
        assertEquals(true, response.account.activeCard)
        assertEquals(7310, response.account.availableLimit)
    }

    @Test
    fun `when account exists with active card and good availbleLimit and receive more than three valid transactionRequests in the same day that doesnt violates frequency in small interval should return without violations`() {
        val validAccount = Account(activeCard = true, availableLimit = 10000)
        accountRepository.save(validAccount)

        val transactionBurgerKing = Transaction(merchant = "Burger King", amount = 20L, time = LocalDateTime.of(2020, 6, 1, 14, 45, 45))
        val requestBurgerKing = TransactionRequest(transactionBurgerKing)

        val transactionAmericanas =
            Transaction(merchant = "Lojas Americanas", amount = 120L, time = LocalDateTime.of(2020, 6, 1, 14, 41, 52))
        val requestAmericanas = TransactionRequest(transactionAmericanas)
        transactionService.process(requestAmericanas)

        val transactionSubmarino =
            Transaction(merchant = "Submarino.com", amount = 2200L, time = LocalDateTime.of(2020, 6, 1, 14, 41, 56))
        val requestSubmarino = TransactionRequest(transactionSubmarino)
        transactionService.process(requestSubmarino)

        val transactionRenner =
            Transaction(merchant = "Lojas Renner", amount = 370L, time = LocalDateTime.of(2020, 6, 1, 14, 41, 59))
        val requestRenner = TransactionRequest(transactionRenner)
        transactionService.process(requestRenner)

        val response = transactionService.process(requestBurgerKing)
        assertTrue(response.violations.isEmpty())
        assertEquals(true, response.account.activeCard)
        assertEquals(7290, response.account.availableLimit)
    }

    @Test
    fun `when account exists with active card and good availbleLimit and receive more than two similar transactionRequests in the same day in small interval should return with violations`() {
        val validAccount = Account(activeCard = true, availableLimit = 10000)
        accountRepository.save(validAccount)

        val transactionBurgerKing = Transaction(merchant = "Burger King", amount = 20L, time = LocalDateTime.of(2020, 6, 1, 14, 45, 45))
        val requestBurgerKing = TransactionRequest(transactionBurgerKing)
        transactionService.process(requestBurgerKing)

        val repeatedTransactionBK = transactionBurgerKing.copy(time = LocalDateTime.of(2020, 6, 1, 14, 46, 45))
        val requestRepeatedBK = TransactionRequest(repeatedTransactionBK)
        transactionService.process(requestRepeatedBK)

        val response = transactionService.process(requestBurgerKing)
        assertTrue(response.violations.size == 1)
        assertEquals(DOUBLED_TRANSACTION, response.violations.first())
        assertEquals(true, response.account.activeCard)
        assertEquals(9960, response.account.availableLimit)
    }
}
