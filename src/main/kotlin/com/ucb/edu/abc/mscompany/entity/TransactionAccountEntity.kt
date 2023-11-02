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
        var entityId: Int?,
        var transactionId: Long,
        var accountId: Long,
        var auxiliaryAccountId: Int?,
        var glosaDetail: String,
        var documentNumber: String?,
        var companyId: Int,
) {
    constructor(): this(0,  0, 0, 0, 0, "", "", 0)
    override fun toString(): String {
        return "TransactionAccountEntity(transactionAccountId=$transactionAccountId, entityId=$entityId, transactionId=$transactionId, accountId=$accountId, auxiliaryAccountId=$auxiliaryAccountId, glosaDetail='$glosaDetail', documentNumber='$documentNumber',  companyId=$companyId)"
    }
}