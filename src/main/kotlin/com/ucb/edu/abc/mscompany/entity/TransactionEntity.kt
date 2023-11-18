package com.ucb.edu.abc.mscompany.entity
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.time.LocalDateTime

@Data
@AllArgsConstructor
@NoArgsConstructor

data class TransactionEntity(
        var transactionId: Long,
        var transactionTypeId: Long,
        var transactionNumber: Long,
        var glosaGeneral: String,
        var date: LocalDateTime,
        var exchangeRateId: Int,
        var areaSubsidiaryId: Int?  ,
        var companyId: Int,
        var userId: Int,
        var ajuste: Boolean
)
{
    constructor(): this(0, 0, 0, "", LocalDateTime.now(), 0, 0, 0, 0, true  )
    override fun toString(): String {
        return "TransactionEntity(transactionId=$transactionId, transactionTypeId=$transactionTypeId, transactionNumber=$transactionNumber, glosaGeneral='$glosaGeneral', date=$date, exchangeRateId=$exchangeRateId, areaSubsidiaryId=$areaSubsidiaryId, companyId=$companyId, userId=$userId)"
    }
}