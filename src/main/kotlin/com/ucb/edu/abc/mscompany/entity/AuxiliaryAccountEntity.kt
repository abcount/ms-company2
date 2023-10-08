package com.ucb.edu.abc.mscompany.entity

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@AllArgsConstructor
@NoArgsConstructor
data class AuxiliaryAccountEntity(
    val auxiliaryAccountId: Int,
    var codeAccount: String,
    var nameDescription: String,
    var companyId: Int,
){
    constructor(): this(0, "", "", 0)

    override fun toString(): String {
        return "AuxiliaryAccountEntity(auxiliaryAccountId=$auxiliaryAccountId, codeAccount='$codeAccount', nameDescription='$nameDescription', companyId=$companyId)"
    }
}
