package com.ucb.edu.abc.mscompany.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.math.BigDecimal
import java.util.*

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class BalanceGeneralResponseDto(
        var companyName: String,
        var dateTo: Date,
        var currency: String,
        var responsible: List<String>,
        var subsidiaries: List<SubsidiaryBalance>
)
{
}

data class SubsidiaryBalance(
        var subsidiaryId: Int,
        var subsidiaryName: String,
        var areas: List<AreaBalance>
){

}

data class AreaBalance(
        var subsidiaryId: Int,
        var areaId: Int,
        var area: String,
        var accounts: List<AccountBalance>,
        var totalActive: BigDecimal,
        var totalPassiveCapital: BigDecimal,
        var totalResult: BigDecimal,
        var totalActiveFinal: BigDecimal,
        var totalResultFinal: BigDecimal

)
{
}

data class AccountBalance(
        var accountCode: String,
        var accountName: String,
        var amount: BigDecimal,
        var children: List<AccountBalance>, //recursivoxd

)
{
}


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class BalanceGeneralResponseDtoPDF(
        var companyName: String,
        var logo :String,
        var dateTo: String,
        var actualDate: String,
        var actualHour: String,
        var userName: String,
        var currency: String,
        var responsible: List<String>,
        var subsidiaries: List<SubsidiaryBalancePDF>
)
{
}

data class SubsidiaryBalancePDF(
        var subsidiaryId: Int,
        var subsidiaryName: String,
        var areas: List<AreaBalancePDF>
){

}

data class AreaBalancePDF(
        var subsidiaryId: Int,
        var areaId: Int,
        var area: String,
        var accounts: List<AccountBalancePDF>,
        var totalActive: String,
        var totalPassiveCapital: String,
        var totalResult:String,
        var totalActiveFinal: String,
        var totalResultFinal: String

)
{
}

data class AccountBalancePDF(
        var accountCode: String,
        var accountName: String,
        var amount: String,
        var children: List<AccountBalancePDF>, //recursivoxd

)
{
}




