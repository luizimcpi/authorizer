package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.common.utils.LocalDateTimeUtils
import com.nu.authorizer.domain.model.entities.Transaction

object TransactionIntervalRules {

    fun existsSimilarTransactionsOnTwoMinuteInterval(transactions: List<Transaction>): Boolean {
        val lastTransactions = transactions.take(3)
        val sameAmount = lastTransactions.map { it.amount }.toList().stream().distinct().count() <= 1
        val sameMerchant = lastTransactions.map { it.merchant }.toList().stream().distinct().count() <= 1

        if (sameAmount && sameMerchant) {
            return existsTransactionsOnTwoMinuteInterval(transactions, true)
        }
        return false
    }

    fun existsTransactionsOnTwoMinuteInterval(transactions: List<Transaction>, similarTransactions: Boolean = false): Boolean {
        val lastTransactions = if (similarTransactions) {
            transactions.take(3)
        } else {
            transactions
        }
        val allTransactionsInTheSameDay =
            if (similarTransactions) lastTransactions.size == 3
            else lastTransactions.size == 4 && lastTransactions.map { it.time.toLocalDate() }.toList()
                .stream()
                .distinct()
                .count() <= 1

        val allTransactionsInTheSameHour = lastTransactions.map { it.time.hour }
            .toList()
            .stream()
            .distinct()
            .count() <= 1

        if (allTransactionsInTheSameDay && allTransactionsInTheSameHour) {
            val lastDateTime = lastTransactions.map { it.time }.toList().first()
            val firstDateTime = lastTransactions.map { it.time }.toList().last()
            if (LocalDateTimeUtils.getDifferenceInMinutes(firstDateTime, lastDateTime) <= 2) return true
        }

        return false
    }
}
