package com.ucb.edu.abc.mscompany.dto.response
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.util.Date
import java.math.BigDecimal
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
        var glosaDetail: String,
        var documentNumber: String?,
        var debitAmount: BigDecimal,
        var creditAmount: BigDecimal,
        var balances: BigDecimal
)