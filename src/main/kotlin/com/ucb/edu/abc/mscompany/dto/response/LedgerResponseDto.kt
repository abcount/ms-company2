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
        var accounts: List<AccountLedger>
)

data class AccountLedger(
        var accountCode: String,
        var accountName: String,
        var transactions: List<Transaction>,
        var totalDebitAmount: BigDecimal,
        var totalCreditAmount: BigDecimal,
        var totalBalances: BigDecimal
)

data class Transaction(
        var voucherCode: Int,
        var subdidary: String,
        var area: String,
        var registrationDate: Date,
        var glosaDetail: String,
        var documentNumber: String,
        var debitAmount: BigDecimal,
        var creditAmount: BigDecimal,
        var balances: BigDecimal
)
