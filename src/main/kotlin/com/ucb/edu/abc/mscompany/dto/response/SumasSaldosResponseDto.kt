package com.ucb.edu.abc.mscompany.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.math.BigDecimal
import java.time.temporal.TemporalAmount
import java.util.*

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class SumasSaldosResponseDto (
    var companyName: String,
    var dateFrom: Date,
    var dateTo: Date,
    var currency: String,
    var subsidiaries: List<SubsidiarySumas>
){

}

data class SubsidiarySumas(
    var subsidiaryId: Int,
    var subsidiaryName: String,
    var areas: List<AreaSumas>
){}

data class AreaSumas(
    var areaId: Int,
    var subsidiaryId: Int,
    var areaName: String,
    var accounts: List<AccountSumas>,
    var totalSumsDebitAmount: BigDecimal,
    var totalSumsCreditAmount: BigDecimal,
    var totalBalancesDebitAmount: BigDecimal,
    var totalBalancesCreditAmount: BigDecimal,
){}

data class AccountSumas(
    var accountCode: String,
    var accountName: String,
    var sumsDebitAmount: BigDecimal,
    var sumsCreditAmount: BigDecimal,
    var balancesDebitAmount: BigDecimal,
    var balancesCreditAmount: BigDecimal,
){}


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class SumasSaldosResponseDtoPdf (
        var companyName: String,
        var logo: String,
        var dateFrom: String,
        var dateTo: String,
        var currency: String,
        var subsidiaries: List<SubsidiarySumasPdf>
){

}

data class SubsidiarySumasPdf(
        var subsidiaryId: Int,
        var subsidiaryName: String,
        var areas: List<AreaSumasPdf>
){}

data class AreaSumasPdf(
        var areaId: Int,
        var subsidiaryId: Int,
        var areaName: String,
        var accounts: List<AccountSumasPdf>,
        var totalSumsDebitAmount: String,
        var totalSumsCreditAmount: String,
        var totalBalancesDebitAmount: String,
        var totalBalancesCreditAmount: String,
){}

data class AccountSumasPdf(
        var accountCode: String,
        var accountName: String,
        var sumsDebitAmount: String,
        var sumsCreditAmount: String,
        var balancesDebitAmount: String,
        var balancesCreditAmount: String,
){}