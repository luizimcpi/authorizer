package com.nu.authorizer.application.view

import com.nu.authorizer.domain.common.config.JacksonConfig
import com.nu.authorizer.domain.common.constants.Constants.ACCOUNT_NAME
import com.nu.authorizer.domain.common.constants.Constants.TRANSACTION_NAME
import com.nu.authorizer.domain.model.requests.AccountRequest
import com.nu.authorizer.domain.model.requests.TransactionRequest
import com.nu.authorizer.domain.services.RouterService

class EventStreamPresenter(
    routerAccountService: RouterService<AccountRequest>,
    routerTransactionService: RouterService<TransactionRequest>
) {

    private val mapServices = mapOf(
        Pair(ACCOUNT_NAME, routerAccountService),
        Pair(TRANSACTION_NAME, routerTransactionService)
    )

    fun printLines(lines: List<String>) {
        lines.forEach {
            try {
                val className = getClassName(it)
                val response = mapServices[className]!!.getResponse(it)
                println(JacksonConfig.toJson(response))
            } catch (e: Exception) {
                println()
            }
        }
    }

    private fun getClassName(line: String): String {
        return if (line.contains(ACCOUNT_NAME)) {
            ACCOUNT_NAME
        } else {
            TRANSACTION_NAME
        }
    }
}
