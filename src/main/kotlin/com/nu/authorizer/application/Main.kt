package com.nu.authorizer.application

import com.nu.authorizer.application.config.JacksonConfig
import com.nu.authorizer.application.config.ObjectMapperConfig
import com.nu.authorizer.application.view.EventStreamPresenter
import com.nu.authorizer.domain.services.AccountService
import com.nu.authorizer.resources.repositories.AccountInMemoryRepository

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        JacksonConfig.configure(ObjectMapperConfig.jsonObjectMapper)
        val repository = AccountInMemoryRepository()
        val accountService = AccountService(repository)

        while (true) {
            val lines = readLines()
            EventStreamPresenter.printLines(lines, accountService)
        }
    }

    private fun readLines(separator: String = "\n") = readLine()!!.split(separator)
}
