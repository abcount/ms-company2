package com.ucb.edu.abc.mscompany.entity

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.sql.Timestamp

@Data
@NoArgsConstructor
@AllArgsConstructor
data class ReportEntity(
    val reportId: Int,
    var date: Timestamp?,
    var userId: Int,
    var companyId: Int,
    var uuid: String,
    var typeDocument: String,
    var typeReport: String,
){
    constructor(): this(0, Timestamp(System.currentTimeMillis()), 0, 0, "","","")
}
