package com.ucb.edu.abc.mscompany.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class TransactionalVoucherDto(
    var transactionNumber: Int,
    var companyName: String,
    var transactionType: List<TransactionType>,
    var subsidiaries: List<Subsidiary>,
    var areas: List<Area>,
    var currencies: List<CurrencyVoucher>,
    var accountablePlan: List<Account>,
    var auxiliar: List<Auxiliary>,
){
    constructor(): this(0, "", listOf(), listOf(), listOf(), listOf(), listOf(), listOf())
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class TransactionType(
    val transactionTypeId: Int,
    val type: String,
    val description: String
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class Subsidiary(
    val subsidiaryId: Int,
    val subsidiaryName: String,
    val subsidiaryAddress: String,
    val editable: Boolean,
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class Area(
    val areaId: Int,
    val areaName: String,
    val editable: Boolean,
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class CurrencyVoucher(
    val exchangeMoneyId: Int,
    val moneyName: String,
    val abbreviationName: String,
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class Account(
    val accountId: Int,
    val accountCode: String,
    val nameAccount: String,
    val report: Boolean,
    val moneyRub: Boolean,
    val classificator: Boolean,
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class Auxiliary(
    val auxiliaryAccountId: Int,
    val codeAccount: String,
    val nameDescription: String,
)
