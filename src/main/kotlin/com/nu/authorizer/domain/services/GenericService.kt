package com.nu.authorizer.domain.services

interface GenericService<T, R> {
    fun create(entity: T): R
}
