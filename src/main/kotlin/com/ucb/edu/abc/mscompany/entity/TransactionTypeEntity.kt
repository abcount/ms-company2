package com.ucb.edu.abc.mscompany.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class TransactionTypeEntity(
    val transactionTypeId: Int,
    var companyId: Int,
    var type: String,
    var description: String,
) {
    constructor(): this(0, 0, "", "")

    override fun toString(): String {
        return "TransactionTypeEntity(transactionTypeId=$transactionTypeId, companyId=$companyId, type='$type', description='$description')"
    }
}