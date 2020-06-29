package com.nu.authorizer.domain.services

interface GenericService<T, R> {
    fun process(request: T): R
}
