package com.ucb.edu.abc.mscompany.entity.pojos

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.util.*

@Data
@NoArgsConstructor
@AllArgsConstructor
data class SubsidiaryAndAreaPojo(
    var areaSubsidiaryId: Int,
    var areaId: Int,
    var subsidiaryId: Int,
    var diccCategory: String,
    var dateCreated: Date,
    var areaName: String,
    var subsidiaryName:String,
    var permissionId: Int?,
) {
}