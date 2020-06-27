package com.nu.authorizer.resources.repositories

import com.nu.authorizer.domain.model.entities.Account
import com.nu.authorizer.domain.repositories.AccountRepository
import java.util.concurrent.atomic.AtomicInteger

class AccountInMemoryRepository: AccountRepository {

    private var accounts = mutableMapOf<Int, Account>()
    private var lastId: AtomicInteger = AtomicInteger(accounts.size - 1)

    override fun getAccount(account: Account): Account? {
        return accounts[0]
    }

    override fun create(account: Account) {
        val id = lastId.incrementAndGet()
        accounts[id] = account
    }
}