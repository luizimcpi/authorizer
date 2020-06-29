package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.common.config.JacksonConfig
import com.nu.authorizer.domain.model.requests.AccountRequest
import com.nu.authorizer.domain.model.requests.TransactionRequest

class RouterService<T, R>(private val genericService: GenericService<T, R>) {

    fun getResponse(line: String): R {
        val classType = getClassType(line)
        val request = JacksonConfig.fromJson(line, classType)
        return genericService.process(request)
    }

    private fun getClassType(line: String): Class<T> {
        return if (line.contains("account")) {
            AccountRequest::class.java as Class<T>
        } else {
            if (line.contains("transaction")) {
                TransactionRequest::class.java as Class<T>
            } else {
                throw Exception("Class type conversion error")
            }
        }
    }
}
