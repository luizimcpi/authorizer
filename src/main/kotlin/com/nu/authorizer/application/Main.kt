package com.nu.authorizer.application

import com.nu.authorizer.application.view.EventStreamPresenter
import com.nu.authorizer.domain.common.config.JacksonConfig
import com.nu.authorizer.domain.common.config.ObjectMapperConfig
import com.nu.authorizer.domain.services.AccountService
import com.nu.authorizer.domain.services.RouterService
import com.nu.authorizer.domain.services.TransactionService
import com.nu.authorizer.resources.repositories.AccountInMemoryRepository
import com.nu.authorizer.resources.repositories.TransactionInMemoryRepository

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        JacksonConfig.configure(ObjectMapperConfig.jsonObjectMapper)
        val accountRepository = AccountInMemoryRepository()
        val accountService = AccountService(accountRepository)
        val routerAccountService = RouterService(accountService)
        val transactionRepository = TransactionInMemoryRepository()
        val transactionService = TransactionService(accountService, transactionRepository)
        val routerTransactionService = RouterService(transactionService)

        while (true) {
            val lines = readLines()
            EventStreamPresenter.printAccountLines(lines, routerAccountService)
            EventStreamPresenter.printTransactionLines(lines, routerTransactionService)
        }
    }

    private fun readLines(separator: String = "\n") = readLine()!!.split(separator)
}
