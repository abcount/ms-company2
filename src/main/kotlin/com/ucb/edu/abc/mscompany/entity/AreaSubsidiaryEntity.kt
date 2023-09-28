package com.ucb.edu.abc.mscompany.entity

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.util.*

@Data
@NoArgsConstructor
@AllArgsConstructor
data class AreaSubsidiaryEntity(
        val areaSubsidiaryId: Int,
        var areaId: Int,
        var subsidiaryId: Int,
        var diccCategory: String,
        var dateCreated: Date,
){
    constructor(): this(0, 0, 0, "", Date())

    override fun toString(): String {
        return "AreaSubsidiaryEntity(areaSubsidiaryId=$areaSubsidiaryId, areaId=$areaId, subsidiaryId=$subsidiaryId, diccCategory='$diccCategory', dateCreated=$dateCreated)"
    }
}
