package com.ucb.edu.abc.mscompany.dto.response

data class UsersAndInvitation(
    var employee: List<Employee>,
    var invitation: List<Invitation>
)
data class Employee(
    var employeeId: Int,
    var name: String,
    var email: String,
    var urlProfilePicture: String
)
data class Invitation(
    var invitationId: Int,
    var invited: String,
    var invitedId: Int,
    var email: String,
    var urlProfilePicture: String
)