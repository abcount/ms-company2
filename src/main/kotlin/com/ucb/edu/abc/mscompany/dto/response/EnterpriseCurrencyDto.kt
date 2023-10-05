package com.ucb.edu.abc.mscompany.dto.response

import com.ucb.edu.abc.mscompany.entity.ExchangeMoneyEntity

data class EnterpriseCurrencyDto(
        val currencyConfig: List<Currency>,
        val openingDate: String,
)

data class Currency(
        val moneyName: String,
        val abbreviationName: String,
)
