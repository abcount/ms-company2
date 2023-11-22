package com.ucb.edu.abc.mscompany.dto.response

import com.ucb.edu.abc.mscompany.entity.AccessPersonEntity
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.time.LocalDate
import java.util.Date

@AllArgsConstructor
@NoArgsConstructor
@Data
data class AccessPersonWithImageDtoResponse (

    var accessPersonId: Long = 0,
    var username: String = "",
    var email: String = "",
    var secret: String? = "",
    var address: String? = "",
    var noFono: String? = "",
    var extNoFono: String? = "",
    var countryIdentity: String? = "",
    var noIdentity: String? = "",
    var extNoIdentity: String? = "",
    var firstName: String? = "",
    var lastName: String? = "",
    var genderPerson: Int = 0,
    var birthday: LocalDate?  = LocalDate.now(),
    var diccCategory: String? = "",
    var dateCreation: Date = Date(),
    var userUuid: String = "",
    var urlImage: String = ""
){

}