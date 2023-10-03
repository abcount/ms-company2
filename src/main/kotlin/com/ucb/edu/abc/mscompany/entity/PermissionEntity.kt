package com.ucb.edu.abc.mscompany.entity

data class PermissionEntity(
    var userId: Int? = null,
    var areaSubsidiaryId: Int? = null,
    var roleId: Int? = null,
    var groupId: Int? = null,
    var companyId: Int? = null
)  {
    fun isComplete(): Boolean{
        if (this.userId == null) return false
        if (this.areaSubsidiaryId == null) return false
        if (this.roleId == null) return false
        if (this.groupId == null) return false
        if (this.companyId == null) return false
        return true
    }

}