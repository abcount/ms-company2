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
    var transactionId: Int,
    var ajuste: Boolean,
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
    var documentCode: String?
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class ListTransactionDtoPdf(
        var logo: String,
        var today: String,
        var subsidiaryName: String,
        var areaName: String,
        var transactionNumber: Int,
        var transactionId: Int,
        var currency: CurrencyVoucher,
        var date: String,
        var glosaGeneral: String,
        var transactions: List<TransactionListDtoPdf>,
        var totalDebit: String,
        var totalCredit: String
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class TransactionListDtoPdf(
        var accountId: Int,
        var accountCode: String,
        var nameAccount: String,
        var entityId: Int?,
        var entityName: String?,
        var auxiliaryId: Int?,
        var codeAccount: String?,
        var amountDebit: String,
        var amountCredit: String,
        var glosaDetail: String,
        var documentCode: String?
)