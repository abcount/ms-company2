package com.ucb.edu.abc.mscompany.entity

data class SubsidiaryEntity(
        val subsidiaryId: Int,
        var companyId: Int,
        var subsidiaryName: String,
        var address: String?,
){
    constructor(): this(0,0,"","")

    override fun toString(): String {
        return "SubsidiaryEntity(subsidiaryId=$subsidiaryId, companyId=$companyId, subsidiaryName='$subsidiaryName', address=$address)"
    }
}
