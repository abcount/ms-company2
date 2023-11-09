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