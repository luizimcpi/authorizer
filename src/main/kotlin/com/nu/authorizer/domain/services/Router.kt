package com.nu.authorizer.domain.services

interface Router {
    fun getResponse(line: String): Any
}
