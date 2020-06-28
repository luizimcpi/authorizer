package com.nu.authorizer.application

import com.nu.authorizer.application.view.EventStreamPresenter
import com.nu.authorizer.domain.common.config.JacksonConfig
import com.nu.authorizer.domain.common.config.ObjectMapperConfig
import com.nu.authorizer.domain.services.AccountService
import com.nu.authorizer.domain.services.RouterService
import com.nu.authorizer.resources.repositories.AccountInMemoryRepository

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        JacksonConfig.configure(ObjectMapperConfig.jsonObjectMapper)
        val repository = AccountInMemoryRepository()
        val accountService = AccountService(repository)
        val routerService = RouterService(accountService)

        while (true) {
            val lines = readLines()
            EventStreamPresenter.printLines(lines, routerService)
        }
    }

    private fun readLines(separator: String = "\n") = readLine()!!.split(separator)
}
