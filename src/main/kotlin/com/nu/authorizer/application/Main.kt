package com.nu.authorizer.application

import com.nu.authorizer.application.config.JacksonConfig
import com.nu.authorizer.application.config.ObjectMapperConfig
import com.nu.authorizer.domain.model.requests.AccountRequest
import com.nu.authorizer.domain.services.AccountService

object Main {

    @JvmStatic
    fun main(args: Array<String>){
        JacksonConfig.configure(ObjectMapperConfig.jsonObjectMapper)

        val lines = generateSequence(readLine()) {
            readLine()
        }

        val accountService = AccountService()

        lines.forEach {
            try {
               val response = accountService.create(JacksonConfig.fromJson(it, AccountRequest::class.java))
                println(response)
            }catch (e: Exception){
                println("Não foi possível parsear a entrada")
            }
        }
    }
}