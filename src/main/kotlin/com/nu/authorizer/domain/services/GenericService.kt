package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.model.responses.AccountResponse

interface GenericService<T> {
    fun process(request: T): AccountResponse
}
