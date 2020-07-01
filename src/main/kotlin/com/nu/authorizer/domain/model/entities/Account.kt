package com.nu.authorizer.domain.model.entities

data class Account(val activeCard: Boolean, var availableLimit: Long) {
    fun debit(amount: Long): Long {
        return availableLimit - amount
    }
}
