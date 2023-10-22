package com.ucb.edu.abc.mscompany.entity.pojos

import com.ucb.edu.abc.mscompany.enums.InvitationState

data class UserAndAccessPersonInformation (
    var userId: Int,
    var firstName: String?,
    var lastName: String?,
    var email: String,
)

data class InvitationAndUserInformation(
    var invitationId: Int,
    var userId: Int,
    var firstName: String,
    var lastName: String,
    var email: String
)

data class PersonalInvitations(
    var invitationId: Int,
    var username: String, // this is the username of person who make the invitation
    var companyName: String, // company name
    var invitationStatus: String,
    var status: Boolean,
    var accessPersonId: Int // invited
)

data class GroupRoleExtendedPojo(
    var groupRoleId: Int,
    var roleId: Int,
    var groupId: Int,
    var name:String,
    var status: Boolean
)