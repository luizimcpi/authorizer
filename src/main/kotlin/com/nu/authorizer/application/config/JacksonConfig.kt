package com.nu.authorizer.application.config

import com.fasterxml.jackson.databind.ObjectMapper

object JacksonConfig {

    private lateinit var objectMapper: ObjectMapper

    @JvmStatic
    fun configure(staticObjectMapper: ObjectMapper) {
        objectMapper = staticObjectMapper
    }

    @JvmStatic
    fun getObjectMapper(): ObjectMapper {
        return (objectMapper)
    }

    fun toJson(`object`: Any): String {
        return (objectMapper).writeValueAsString(`object`)
    }

    fun <T> fromJson(json: String, clazz: Class<T>): T {
        return (objectMapper).readValue(json, clazz)
    }
}