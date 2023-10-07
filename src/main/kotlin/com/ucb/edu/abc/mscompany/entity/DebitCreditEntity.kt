package com.ucb.edu.abc.mscompany.entity

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.math.BigDecimal

@Data
@AllArgsConstructor
@NoArgsConstructor
data class DebitCreditEntity (
        var debitCreditId: Long,
        var transactionAccountId: Long,
        var amountCredit: BigDecimal,
        var amountDebit: BigDecimal,
        var exchangeRateId: Int,

)
{
    constructor(): this(0, 0, BigDecimal.ZERO, BigDecimal.ZERO, 0)
    override fun toString(): String {
        return "DebitCreditEntity(debitCreditId=$debitCreditId, transactionAccountId=$transactionAccountId, amountCredit=$amountCredit, amountDebit=$amountDebit, exchangeRateId=$exchangeRateId)"
    }
}