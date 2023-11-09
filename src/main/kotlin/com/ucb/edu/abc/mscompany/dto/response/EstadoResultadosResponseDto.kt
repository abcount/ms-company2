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
data class EstadoResultadosResponseDto(
        var companyName: String,
        var dateTo: Date,
        var currency: String,
        var responsible: List<String>,
        var subsidiaries: List<SubsidiaryState>
)
{
}

data class SubsidiaryState(
        var subsidiaryId: Int,
        var subsidiaryName: String,
        var areas: List<AreaState>
){

}

data class AreaState(
        var subsidiaryId: Int,
        var areaId: Int,
        var area: String,
        var accounts: List<AccountState>,
        var amountTotal: BigDecimal,
)
{
}

data class AccountState(
        var accountCode: String,
        var accountName: String,
        var amount: BigDecimal,
        var children: List<AccountState>, //recursivoxd

)
{
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class EstadoResultadosResponseDtoPDF(
        var companyName: String,
        var dateTo: String,
        var currency: String,
        var responsible: List<String>,
        var subsidiaries: List<SubsidiaryStatePDF>
)
{
}

data class SubsidiaryStatePDF(
        var subsidiaryId: Int,
        var subsidiaryName: String,
        var areas: List<AreaStatePDF>
){

}

data class AreaStatePDF(
        var subsidiaryId: Int,
        var areaId: Int,
        var area: String,
        var accounts: List<AccountStatePDF>,
        var amountTotal: BigDecimal,
)
{
}

data class AccountStatePDF(
        var accountCode: String,
        var accountName: String,
        var amount: BigDecimal,
        var children: List<AccountStatePDF>, //recursivoxd

)
{
}





