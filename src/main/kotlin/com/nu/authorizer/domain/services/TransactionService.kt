package com.nu.authorizer.domain.services

import com.nu.authorizer.domain.exception.AccountNotFoundException
import com.nu.authorizer.domain.model.entities.Account
import com.nu.authorizer.domain.model.requests.TransactionRequest
import com.nu.authorizer.domain.model.responses.AccountResponse
import com.nu.authorizer.domain.repositories.AccountRepository
import com.nu.authorizer.domain.repositories.TransactionRepository

class TransactionService(
    private val accountRepository: AccountRepository,
    private val repository: TransactionRepository
) : GenericService<TransactionRequest> {

    override fun process(transactionRequest: TransactionRequest): AccountResponse {
        repository.save(transactionRequest.transaction)
        val account = accountRepository.getAccount()
        val violations = mutableListOf<String>()
        if (account != null) {
            checkViolations(transactionRequest, account, violations)
            if (violations.isEmpty()) {
                val debitAccount = account.copy(availableLimit = account.debit(transactionRequest.transaction.amount))
                accountRepository.update(debitAccount, 0)
                return AccountResponse(account = debitAccount, violations = violations)
            }
            return AccountResponse(account = account, violations = violations)
        }

        throw AccountNotFoundException("Account Not Found")
    }

    private fun checkViolations(
        transactionRequest: TransactionRequest,
        account: Account,
        violations: MutableList<String>
    ) {
        if (transactionRequest.transaction.amount > account.availableLimit) {
            violations.add("insufficient-limit") // passar para constantes
        }
        if (!account.activeCard) {
            violations.add("card-not-active") // passar para constantes
        }
    }
}
