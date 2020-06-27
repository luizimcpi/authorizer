package com.nu.authorizer.application.view

import com.nu.authorizer.application.config.JacksonConfig
import com.nu.authorizer.domain.model.requests.AccountRequest
import com.nu.authorizer.domain.services.AccountService

object EventStreamPresenter {

    fun printLines(lines: List<String>, accountService: AccountService) {
        lines.forEach {
            try {
                val response = accountService.create(JacksonConfig.fromJson(it, AccountRequest::class.java))
                println(JacksonConfig.toJson(response))
            } catch (e: Exception) {
                println("Não foi possível parsear a entrada")
            }
        }
    }
}
