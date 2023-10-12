package com.ucb.edu.abc.mscompany.entity

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@AllArgsConstructor
@NoArgsConstructor
data class InvitationEntity (
    var invitationId : Int = 0,
    var userId: Int = 0,
    var companyId: Int = 0,
    var invitationStatus: String = "",
    var status: Boolean = false,
    var accessPersonId: Int = 0
) {
}