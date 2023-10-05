package com.ucb.edu.abc.mscompany.entity

data class ExchangeEntity(
        val exchangeId: Int,
        var moneyName: String,
        var moneyIso:String,
        var country: String,
){
    constructor(): this(0,"","","")

    override fun toString(): String {
        return "ExchangeEntity(exchangeId=$exchangeId, moneyName='$moneyName', moneyIso='$moneyIso', country='$country')"
    }
}
