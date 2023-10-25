package com.ucb.edu.abc.mscompany.dto.response

data class UsersAndInvitation(
    var employee: List<Employee>,
    var invitation: MutableList<InvitationDto>
)
data class Employee(
    var employeeId: Int,
    var name: String,
    var email: String,
    var urlProfilePicture: String?
)
data class InvitationDto(
    var invitationId: Int,
    var invited: String,
    var invitedId: Long,
    var email: String,
    var urlProfilePicture: String?
)