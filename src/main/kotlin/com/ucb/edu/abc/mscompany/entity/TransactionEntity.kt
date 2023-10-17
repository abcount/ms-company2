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
        var transactionTypeId: Int,
        var transactionNumber: Long,
        var glosaGeneral: String,
        var date: LocalDateTime,
        var exchangeRateId: Int,
        var areaSubsidiaryId: Int?  ,
        var companyId: Int,
        var userId: Int,
)
{
    constructor(): this(0, 0, 0, "", LocalDateTime.now(), 0, 0, 0, 0  )
    override fun toString(): String {
        return "TransactionEntity(transactionId=$transactionId, transactionTypeId=$transactionTypeId, transactionNumber=$transactionNumber, glosaGeneral='$glosaGeneral', date=$date, exchangeRateId=$exchangeRateId, areaSubsidiaryId=$areaSubsidiaryId, companyId=$companyId, userId=$userId)"
    }
}