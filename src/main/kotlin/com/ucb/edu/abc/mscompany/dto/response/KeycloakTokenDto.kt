package com.ucb.edu.abc.mscompany.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class KeycloakTokenDto(
    var access_token: String,
    var expires_in: Int,
    var refresh_expires_in: Int,
    var refresh_token: String,
    var token_type: String,
    var not_before_policy: Int,
    var session_state: String,
    var scope: String

) {
    override fun toString(): String {
        return "KeycloakTokenDto(access_token='$access_token', expires_in=$expires_in, refresh_expires_in=$refresh_expires_in, refresh_token='$refresh_token', token_type='$token_type', not_before_policy=$not_before_policy, session_state='$session_state', scope='$scope')"
    }
}