package com.ucb.edu.abc.mscompany.entity

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.time.LocalDateTime
import java.util.Date

@Data
@AllArgsConstructor
@NoArgsConstructor


data class TransactionAccountEntity(
        var transactionAccountId: Long,
        var userId: Int,
        var entityId: Int,
        var transactionId: Long,
        var accountId: Long,
        var auxiliaryAccountId: Int,
        var glosaDetail: String,
        var documentNumber: String,
        var dueDate: Date,
        var companyId: Int,
) {
    constructor(): this(0, 0, 0, 0, 0, 0, "", "", Date(), 0)
    override fun toString(): String {
        return "TransactionAccountEntity(transactionAccountId=$transactionAccountId, userId=$userId, entityId=$entityId, transactionId=$transactionId, accountId=$accountId, auxiliaryAccountId=$auxiliaryAccountId, glosaDetail='$glosaDetail', documentNumber='$documentNumber', dueDate=$dueDate, companyId=$companyId)"
    }
}