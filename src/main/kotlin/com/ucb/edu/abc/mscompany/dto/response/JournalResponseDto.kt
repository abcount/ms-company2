package com.ucb.edu.abc.mscompany.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Date

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class JournalResponseDto (
        var companyName: String,
        var dateFrom: Date,
        var dateTo: Date,
        var currency: String,
        var subsidiaries: MutableList<SubsidiaryDto>
){}
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class SubsidiaryDto(
        val subsidiaryId: Int,
        val subsidiaryName: String,
        val areas: List<AreaDto>
){}

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class AreaDto(
        val areaId: Int,
        val areaName: String,
        val transactions: List<TransactionDto>
){}
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class TransactionDto(
        val transactionNumber: Long,
        val transactionType: String?,
        val registrationDate: LocalDateTime,
        val exchangeRate: BigDecimal,
        val glosaGeneral: String,
        val accounts: List<AccountDto>,
        val totalDebitAmount: BigDecimal,
        val totalCreditAmount: BigDecimal,
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class AccountDto(
        val codeAccount: String,
        val nameAccount: String,
        val glosaDetail: String,
        val debitAmount: BigDecimal,
        val creditAmount: BigDecimal
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class JournalResponseDtoPdf (
        var companyName: String,
        var dateFrom: String,
        var logo: String,
        var userName: String,
        var actualDate: String,
        var actualHour: String,
        var transactionType: String,
        var dateTo: String,
        var currency: String,
        var subsidiaries: MutableList<SubsidiaryDtoPDF>
){}

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class SubsidiaryDtoPDF(
        val subsidiaryId: Int,
        val subsidiaryName: String,
        val areas: List<AreaDtoPDF>
){}

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class AreaDtoPDF(
        val areaId: Int,
        val areaName: String,
        val transactions: List<TransactionDtoPDF>
){}
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class TransactionDtoPDF(
        val transactionNumber: Long,
        val transactionType: String?,
        val registrationDate: String,
        val exchangeRate: String,
        val glosaGeneral: String,
        val accounts: List<AccountDtoPDF>,
        val totalDebitAmount: String,
        val totalCreditAmount: String,
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class AccountDtoPDF(
        val codeAccount: String,
        val nameAccount: String,
        val glosaDetail: String,
        val debitAmount: String,
        val creditAmount: String
)