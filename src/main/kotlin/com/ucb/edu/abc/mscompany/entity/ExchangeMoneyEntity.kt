package com.ucb.edu.abc.mscompany.entity

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@AllArgsConstructor
data class ExchangeMoneyEntity(
        var exchangeMoneyId: Int,
        var companyId: Int,
        var moneyName: String,
        var abbreviationName: String,
        var isPrincipal: Boolean,
){
    constructor(): this (0, 0, "", "", false)

    override fun toString(): String {
        return "ExchangeMoneyEntity(exchangeMoneyId=$exchangeMoneyId, companyId=$companyId, moneyName='$moneyName', abbreviationName='$abbreviationName', isPrincipal=$isPrincipal)"
    }
}

