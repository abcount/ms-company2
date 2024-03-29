package com.ucb.edu.abc.mscompany.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.ucb.edu.abc.mscompany.entity.ExchangeMoneyEntity
import lombok.Data
import java.math.BigDecimal


@JsonIgnoreProperties(ignoreUnknown = true)
data class ExchangeDateDto(
        val date: String,
        val currencies: List<CurrencyDateDto>
)

data class CurrencyDateDto(
        val exchangeRateId: Int,
        val currency: BigDecimal,
        val moneyName: String,
        val abbreviationName: String
)


data class ListExchangeRateDateDto(
        val exchangeMoney: List<ExchangeMoneyEntity>,
        val exchangeRate: List<ExchangeDateDto>
)

