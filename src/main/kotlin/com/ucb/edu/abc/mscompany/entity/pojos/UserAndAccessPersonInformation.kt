package com.ucb.edu.abc.mscompany.entity.pojos

data class UserAndAccessPersonInformation (
    var userId: Int,
    var firstName: String,
    var lastName: String,
    var email: String,
)

data class InvitationAndUserInformation(
    var invitationId: Int,
    var userId: Int,
    var firstName: String,
    var lastName: String,
    var email: String
)