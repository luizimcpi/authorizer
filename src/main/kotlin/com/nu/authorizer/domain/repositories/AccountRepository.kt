package com.nu.authorizer.domain.repositories

import com.nu.authorizer.domain.model.entities.Account

interface AccountRepository {
    fun getAccount(account: Account): Account?
    fun create(account: Account)
}