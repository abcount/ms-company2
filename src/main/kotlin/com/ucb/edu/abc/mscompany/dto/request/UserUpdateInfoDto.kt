package com.ucb.edu.abc.mscompany.dto.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserUpdateInfoDto(
    var  imageProfile: MultipartFile? = null,
    var  birthday: String?  = null,
    var  names: String? = null,
    var  lastnames: String? = null,
    var  gender: String? = null,
    var  address: String? = null,
    var  phoneNumber: String? = null,
    var  domainNumber: String? = null,
    var  country: String? = null,
    var  dni: String? = null,
    var  dniExtension: String? = null,
){

}
