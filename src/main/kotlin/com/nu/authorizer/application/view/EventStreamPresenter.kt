package com.nu.authorizer.application.view

import com.nu.authorizer.domain.common.config.JacksonConfig
import com.nu.authorizer.domain.model.requests.AccountRequest
import com.nu.authorizer.domain.model.requests.TransactionRequest
import com.nu.authorizer.domain.model.responses.AccountResponse
import com.nu.authorizer.domain.services.RouterService

object EventStreamPresenter {

    fun printAccountLines(lines: List<String>, routerService: RouterService<AccountRequest, AccountResponse>) {
        lines.forEach {
            val response = routerService.getResponse(it)
            println(JacksonConfig.toJson(response))
        }
    }

    fun printTransactionLines(
        lines: List<String>,
        routerService: RouterService<TransactionRequest, AccountResponse>
    ) {
        lines.forEach {
            val response = routerService.getResponse(it)
            println(JacksonConfig.toJson(response))
        }
    }
}
