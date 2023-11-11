package com.ucb.edu.abc.mscompany.dto.response
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class LedgerResponseDto(
        var companyName: String,
        var dateFrom: Date,
        var dateTo: Date,
        var currency: String,
        var subsidiaries: List<SubsidiaryLedger>
)
data class SubsidiaryLedger(
        val subsidiaryId: Int,
        val subsidiaryName: String,
        val areas: List<AreaLedger      >
){}


data class AreaLedger(
        var areaId: Int,
        var area: String,
        var accounts: List<AccountLedger>
)
data class AccountLedger(
        var accountCode: String,
        var accountName: String,
        var transactions: List<TransactionLedger>,
        var totalDebitAmount: BigDecimal,
        var totalCreditAmount: BigDecimal,
        var totalBalances: BigDecimal
)

data class TransactionLedger(
        var voucherCode: Int,
        var registrationDate: Date,
        var transactionType: String,
        var glosaDetail: String,
        var documentNumber: String?,
        var debitAmount: BigDecimal,
        var creditAmount: BigDecimal,
        var balances: BigDecimal
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class LedgerResponseDtoPdf(
        var companyName: String,
        var dateFrom: String,
        var logo: String,
        var dateTo: String,
        var currency: String,
        var subsidiaries: List<SubsidiaryLedgerPdf>
)
data class SubsidiaryLedgerPdf(
        val subsidiaryId: Int,
        val subsidiaryName: String,
        val areas: List<AreaLedgerPdf>
){}


data class AreaLedgerPdf(
        var areaId: Int,
        var area: String,
        var accounts: List<AccountLedgerPdf>
)
data class AccountLedgerPdf(
        var accountCode: String,
        var accountName: String,
        var transactions: List<TransactionLedgerPdf>,
        var totalDebitAmount: String,
        var totalCreditAmount: String,
        var totalBalances: String
){
}

data class TransactionLedgerPdf(
        var voucherCode: Int,
        var registrationDate: String,
        var transactionType: String,
        var glosaDetail: String,
        var documentNumber: String?,
        var debitAmount: String,
        var creditAmount: String,
        var balances: String
){

}




