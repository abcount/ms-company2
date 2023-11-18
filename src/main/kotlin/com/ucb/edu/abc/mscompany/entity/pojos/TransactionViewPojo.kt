package com.ucb.edu.abc.mscompany.entity.pojos

import com.ucb.edu.abc.mscompany.dto.response.CurrencyVoucher
import java.sql.Timestamp

data class TransactionViewPojo(
    var transactionId: Int,
    var transactionNumber: Int,
    var exchangeRateId: Int,
    var date: Timestamp,
    var glosaGeneral: String,
)
