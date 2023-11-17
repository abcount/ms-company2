package com.ucb.edu.abc.mscompany.entity

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.math.BigDecimal
import java.sql.Timestamp

@Data
@NoArgsConstructor
@AllArgsConstructor
data class ExchangeRateEntity(
        val exchangeRateId: Int,
        var moneyName: String,
        var companyId: Int,
        var currency: BigDecimal,
        var abbreviationName: String,
        var date: Timestamp
)
