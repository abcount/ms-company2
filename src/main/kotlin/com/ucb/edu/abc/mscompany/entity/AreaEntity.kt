package com.ucb.edu.abc.mscompany.entity

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.util.Date

@Data
@NoArgsConstructor
@AllArgsConstructor
data class AreaEntity(
        val areaId: Int,
        var companyId: Int,
        var areaName: String,
        var dateCreated: Date,
        var status: Boolean,
        var commonId: Int?
){
    constructor(): this(0, 0, "", Date(), false, 0)
}
