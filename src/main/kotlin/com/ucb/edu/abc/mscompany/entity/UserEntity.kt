package com.ucb.edu.abc.mscompany.entity

import java.time.LocalDate

data class UserEntity (
    var userId: Int = 0,
    var accessPersonId: Int = 0,
    var diccCategory: String? = null,
    var dateCreated: LocalDate? =  null,
){
    override fun toString(): String {
        return "UserEntity(userId=$userId, accessPersonId=$accessPersonId, diccCategory=$diccCategory, dateCreated=$dateCreated)"
    }
}