package com.nu.authorizer.application.view

import com.nu.authorizer.domain.common.config.JacksonConfig
import com.nu.authorizer.domain.services.RouterService

object EventStreamPresenter {

    fun printLines(lines: List<String>, routerService: RouterService) {
        lines.forEach {
            val response = routerService.getResponse(it)
            println(JacksonConfig.toJson(response))
        }
    }
}
