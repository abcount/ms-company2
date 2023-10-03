package com.ucb.edu.abc.mscompany.entity

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.time.LocalDate

@Data
@NoArgsConstructor
@AllArgsConstructor
data class AccessPersonEntity (
    var accessPersonId: Long = 0,
    var username: String = "",
    var email: String = "",
    var secret: String? = null,
    var address: String? = null,
    var noFono: String? = null,
    var extNoFono: String? = null,
    var countryIdentity: String? = null,
    var noIdentity: String? = null,
    var extNoIdentity: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var genderPerson: Int = 0,
    var birthday: LocalDate?  = null,
    var diccCategory: String? = null,
    var dateCreation: LocalDate = LocalDate.now(),
    var userUuid: String = "",
){
}