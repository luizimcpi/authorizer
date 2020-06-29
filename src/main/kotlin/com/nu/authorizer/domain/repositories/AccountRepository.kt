package com.nu.authorizer.domain.repositories

import com.nu.authorizer.domain.model.entities.Account

interface AccountRepository {
    fun getAccount(): Account?
    fun save(account: Account)
    fun update(account: Account, id: Int)
}
