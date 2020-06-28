package com.nu.authorizer.domain.model.entities

import java.time.LocalDateTime

data class Transaction(val merchant: String, val amount: Long, val time: LocalDateTime)
