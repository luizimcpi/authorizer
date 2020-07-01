package com.nu.authorizer.resources.respositories

import com.nu.authorizer.domain.model.entities.Transaction
import com.nu.authorizer.resources.repositories.TransactionInMemoryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class TransactionInMemoryRepositoryTests {

    private lateinit var transactionRepository: TransactionInMemoryRepository

    @BeforeEach
    fun setUp() {
        transactionRepository = TransactionInMemoryRepository()
    }

    @Test
    fun `when receive valid transaction should create with success`() {
        val validTransaction = Transaction(merchant = "Burger King", amount = 20L, time = LocalDateTime.now())
        transactionRepository.save(validTransaction)
        assertTrue(transactionRepository.getLastTransactions().isNotEmpty())
    }

    @Test
    fun `when receive some valid transactions should get last three transactions ordered by desc time`() {
        val transactionBurgerKing = Transaction(merchant = "Burger King", amount = 20L, time = LocalDateTime.now())
        transactionRepository.save(transactionBurgerKing)
        val transactionAmericanas = Transaction(merchant = "Lojas Americanas", amount = 120L, time = LocalDateTime.of(2020, 6, 1, 14, 45))
        transactionRepository.save(transactionAmericanas)
        val transactionSubmarino = Transaction(merchant = "Submarino.com", amount = 2200L, time = LocalDateTime.of(2020, 6, 1, 11, 19))
        transactionRepository.save(transactionSubmarino)
        val transactionRenner = Transaction(merchant = "Lojas Renner", amount = 370L, time = LocalDateTime.of(2020, 5, 1, 13, 45))
        transactionRepository.save(transactionRenner)
        val transactionSpotify = Transaction(merchant = "Spotify", amount = 40L, time = LocalDateTime.of(2020, 1, 1, 19, 19))
        transactionRepository.save(transactionSpotify)
        val transactionAmazon = Transaction(merchant = "Amazon Prime", amount = 670L, time = LocalDateTime.of(2020, 2, 1, 12, 45))
        transactionRepository.save(transactionAmazon)

        val lastTransactions = transactionRepository.getLastTransactions()

        assertTrue(lastTransactions.size == 4)
        assertEquals("Burger King", lastTransactions.first().merchant)
        assertEquals("Lojas Americanas", lastTransactions[1].merchant)
        assertEquals("Submarino.com", lastTransactions[2].merchant)
        assertEquals("Lojas Renner", lastTransactions[3].merchant)
    }
}
