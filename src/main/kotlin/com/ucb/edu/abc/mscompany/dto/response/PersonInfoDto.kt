package com.ucb.edu.abc.mscompany.dto.response

import com.ucb.edu.abc.mscompany.entity.pojos.PersonalInvitations
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@AllArgsConstructor
@NoArgsConstructor
data class PersonInfoDto(
    var id: Int,
    var fullName:String,
    var userName: String,
    var email: String,
    var imagePath: String,
)