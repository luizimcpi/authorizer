package com.nu.authorizer.application.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object ObjectMapperConfig {
    val jsonObjectMapper: ObjectMapper = configJsonMapper()

    private fun configJsonMapper(): ObjectMapper = jacksonObjectMapper().apply {
        this.registerKotlinModule()
    }
}