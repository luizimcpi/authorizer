package com.nu.authorizer.domain.model.responses

import com.nu.authorizer.domain.model.entities.Account

data class AccountResponse(val account: Account, val violations: List<String> = emptyList())