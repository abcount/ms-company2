package com.ucb.edu.abc.mscompany.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class KeycloakUserDto (
    var id: String,
    var createdTimestamp: Long,
    var username: String,
    var enabled: Boolean,
    var totp: Boolean,
    var emailVerified: Boolean,
    var firstName: String,
    var lastName: String,
    var email: String,
    var attributes: MutableMap<String, MutableList<String>>,
    var disableableCredentialTypes: ArrayList<Any>,
    var requiredActions: ArrayList<Any>,
    var federatedIdentities: ArrayList<Any>?,
    var notBefore: Long,
    var access: Map<String, *>

){
}