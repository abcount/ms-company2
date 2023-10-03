package com.ucb.edu.abc.mscompany.utils

import org.keycloak.TokenVerifier
import org.keycloak.representations.AccessToken
import org.springframework.stereotype.Component

@Component
class KeycloakMasterUtil (
    private var tokenObject:AccessToken? = null,
    private var tokenString: String? = null,
) {
    fun getTokenObject():AccessToken?{
        return this.tokenObject;
    }

    fun getTokenString(): String?{
        return this.tokenString;
    }
    fun updateToken(newToken:String){
        this.tokenObject = TokenVerifier.create(newToken, AccessToken::class.java).token;
        this.tokenString = newToken;
    }

}