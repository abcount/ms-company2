package com.ucb.edu.abc.mscompany.dto.request

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@AllArgsConstructor
@NoArgsConstructor
data class UserUpdateInfoDto(
    var  imageProfile: MultipartFile?,
    var  birthday: String?,
    var  names: String?,
    var  lastnames: String?,
    var  gender: String?,
    var  address: String?,
    var  phoneNumber: String?,
    var  domainNumber: String?,
    var  country: String?,
    var  dni: String?,
    var  dniExtension: String?,
){

}
