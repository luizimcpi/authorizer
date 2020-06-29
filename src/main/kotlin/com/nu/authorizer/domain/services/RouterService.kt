package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.common.config.JacksonConfig
import com.nu.authorizer.domain.exception.AccountNotFoundException
import com.nu.authorizer.domain.exception.ClassTypeConversionException
import com.nu.authorizer.domain.model.requests.AccountRequest
import com.nu.authorizer.domain.model.requests.TransactionRequest
import com.nu.authorizer.domain.model.responses.AccountResponse

class RouterService<T>(private val genericService: GenericService<T>) {

    fun getResponse(line: String): AccountResponse {
        try {
            val classType = getClassType(line)
            val request = JacksonConfig.fromJson(line, classType)
            return genericService.process(request)
        } catch (e: AccountNotFoundException) {
            throw AccountNotFoundException("Account not found please create an account before start a transaction")
        } catch (e: Exception) {
            throw ClassTypeConversionException("Class type conversion error, check your json request")
        }
    }

    private fun getClassType(line: String): Class<T> {
        return if (line.contains("account")) {
            AccountRequest::class.java as Class<T>
        } else {
            if (line.contains("transaction")) {
                TransactionRequest::class.java as Class<T>
            } else {
                throw ClassTypeConversionException("Class type conversion error, check your json request")
            }
        }
    }
}
