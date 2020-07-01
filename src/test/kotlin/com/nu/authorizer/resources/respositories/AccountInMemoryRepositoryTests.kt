package com.nu.authorizer.resources.respositories

import com.nu.authorizer.domain.model.entities.Account
import com.nu.authorizer.resources.repositories.AccountInMemoryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AccountInMemoryRepositoryTests {

    private lateinit var accountRepository: AccountInMemoryRepository

    @BeforeEach
    fun setUp() {
        accountRepository = AccountInMemoryRepository()
    }

    @Test
    fun `when receive first time account should return null`() {
        assertNull(accountRepository.getAccount())
    }

    @Test
    fun `when receive valid account should create with success`() {
        val validAccount = Account(activeCard = true, availableLimit = 100)
        accountRepository.save(validAccount)
        assertNotNull(accountRepository.getAccount())
    }

    @Test
    fun `when receive valid account should update with success`() {
        val validAccount = Account(activeCard = true, availableLimit = 100)
        accountRepository.save(validAccount)
        val updatedAccount = validAccount.copy(false, 1000)
        accountRepository.update(account = updatedAccount, id = 0)
        val result = accountRepository.getAccount()

        assertEquals(false, result?.activeCard)
        assertEquals(1000, result?.availableLimit)
    }
}
