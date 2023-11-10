package com.ucb.edu.abc.mscompany.entity

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.time.LocalDateTime

@Data
@AllArgsConstructor
@NoArgsConstructor
class ClosingSheetEntity
(
   var closingSheetId: Int,
   var companyId: Int,
   var userId: Int,
   var description: String,
   var status: Boolean,
   var date: LocalDateTime,

) {
    constructor(): this(0, 0, 0, "", true ,LocalDateTime.now())



    override fun toString(): String {
         return "ClosingSheetEntity(closingSheetId=$closingSheetId, companyId=$companyId, userId=$userId, description='$description', date=$date)"
    }
}