package com.nu.authorizer.application.view

import com.nu.authorizer.domain.common.config.JacksonConfig
import com.nu.authorizer.domain.common.constants.PresenterConstants.ACCOUNT_REQUEST_CLASS_NAME
import com.nu.authorizer.domain.common.constants.PresenterConstants.TRANSACTION_REQUEST_CLASS_NAME
import com.nu.authorizer.domain.model.requests.AccountRequest
import com.nu.authorizer.domain.model.requests.TransactionRequest
import com.nu.authorizer.domain.services.RouterService

class EventStreamPresenter(
    routerAccountService: RouterService<AccountRequest>,
    routerTransactionService: RouterService<TransactionRequest>
) {

    private val mapServices = mapOf(
        Pair(ACCOUNT_REQUEST_CLASS_NAME, routerAccountService),
        Pair(TRANSACTION_REQUEST_CLASS_NAME, routerTransactionService)
    )

    fun printLines(lines: List<String>) {
        lines.forEach {
            val className = getClassName(it)
            val response = mapServices[className]!!.getResponse(it)
            println(JacksonConfig.toJson(response))
        }
    }

    private fun getClassName(line: String): String {
        return if (line.contains("account")) {
            ACCOUNT_REQUEST_CLASS_NAME
        } else {
            if (line.contains("transaction")) {
                TRANSACTION_REQUEST_CLASS_NAME
            } else {
                throw Exception("Class type conversion error")
            }
        }
    }
}
