package com.ucb.edu.abc.mscompany.entity

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@AllArgsConstructor
data class PermissionEntity(
    var permissionId: Int,
    var areaSubsidiaryId: Int,
    var userId: Int,
    var status: Boolean = false,
    var dicCategory: String? = null
)  {

}