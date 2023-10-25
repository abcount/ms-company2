package com.ucb.edu.abc.mscompany.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class ListTransactionDto(
    var transactionNumber: Int,
    var currency: CurrencyVoucher,
    var date: String,
    var glosaGeneral: String,
    var transactions: List<TransactionListDto>,
    var totalDebit: Double,
    var totalCredit: Double
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class TransactionListDto(
    var accountId: Int,
    var accountCode: String,
    var nameAccount: String,
    var entityId: Int?,
    var entityName: String?,
    var auxiliaryId: Int?,
    var codeAccount: String?,
    var amountDebit: Double,
    var amountCredit: Double,
    var glosaDetail: String,
    var documentCode: String
)
