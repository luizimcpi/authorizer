package com.nu.authorizer.domain.services

import com.fasterxml.jackson.databind.JsonMappingException
import com.nu.authorizer.domain.common.config.JacksonConfig
import com.nu.authorizer.domain.model.requests.AccountRequest
import java.lang.Exception

class RouterService(private val accountService: AccountService) : Router {

    override fun getResponse(line: String): Any {
        var response: Any = "{ invalid-request }"

        try {
            response = accountService.create(JacksonConfig.fromJson(line, AccountRequest::class.java))
        } catch (e: JsonMappingException) {
            return response
        } catch (e: Exception) {
            return response
        }

        return response
    }
}
