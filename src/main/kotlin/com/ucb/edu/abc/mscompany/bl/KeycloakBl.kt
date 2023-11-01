package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dto.response.KeycloakTokenDto
import com.ucb.edu.abc.mscompany.dto.response.KeycloakUserDto
import com.ucb.edu.abc.mscompany.dto.response.RolesKcDto
import com.ucb.edu.abc.mscompany.exception.DuplicateRoleInRealmException
import com.ucb.edu.abc.mscompany.exception.FailedExtractInfoFromKeycloak
import com.ucb.edu.abc.mscompany.exception.FailedExtractInfoTokenException
import com.ucb.edu.abc.mscompany.repository.KeycloakRepository
import com.ucb.edu.abc.mscompany.utils.KeycloakMasterUtil
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.keycloak.TokenVerifier
import org.keycloak.representations.AccessToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import kotlin.jvm.Throws

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
class KeycloakBl @Autowired constructor(
    private val keycloakRepository: KeycloakRepository,
    private val keycloakMasterTokenManager: KeycloakMasterUtil
) {
    // test
    //private val keycloakMasterTokenManager: KeycloakMasterUtil = KeycloakMasterUtil()

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Value("\${custom-config.master-access-key}")
    lateinit var username: String

    @Value("\${custom-config.master-secret-key}")
    lateinit var password: String

    fun getAccessTokenObject(token:String): AccessToken {
        return TokenVerifier.create(token, AccessToken::class.java).token

    }
    fun getKeycloakIdFromToken(token:String): String? {
        try {
            val tokenValue = getAccessTokenObject(token)
            return tokenValue.subject
        }catch (ex: Exception){
            logger.error("#getKeycloakIdFromToken:  There were problems to extract SubjectId from token $token")
            throw FailedExtractInfoTokenException("Cannot extract subject id");
        }
    }
    @Throws(FailedExtractInfoFromKeycloak::class)
    private fun getMasterToken():String {
        if( keycloakMasterTokenManager.getTokenString().isNullOrBlank()
            || keycloakMasterTokenManager.getTokenObject()!!.isExpired){
            keycloakMasterTokenManager.updateToken(getKeycloakDtoMaster().access_token)
        }
        if (! keycloakMasterTokenManager.getTokenString().isNullOrBlank()){
            return keycloakMasterTokenManager.getTokenString()!!;
        }

        throw FailedExtractInfoFromKeycloak("Something went wrong with Keycloak");

    }
    fun getUserInfoFromKeycloak(userId: String): KeycloakUserDto {
        try{
            val masterToken = getMasterToken()
            logger.info("#getUserInfoFromKeycloak: solicita informacion de keycloak, fue a API-KEYCLOAK")
            val kcClient = keycloakRepository.getUserInformation(
                "Bearer $masterToken",
                userId
            )


            return kcClient
        }catch (ex: Exception){
            throw FailedExtractInfoFromKeycloak("Cannot get info about $userId :user from keycloak Exception: ${ex.message}");
        }

    }


    private fun getKeycloakDtoMaster() : KeycloakTokenDto {
        try{
            val mapBody: Map<String,String> = mapOf(
                "client_id" to "admin-cli",
                "username" to username,
                "password" to password,
                "grant_type" to "password"
            )
            return  keycloakRepository.getMasterToken(mapBody)
        }catch (ex: Exception){
            throw FailedExtractInfoFromKeycloak("Cannot get master token");
        }

    }


    fun updateUserStateFromKeycloak(userUuid: String, state: Boolean){
        val mapBody: Map<String, Any> =  mapOf(
            "enabled" to false
        )
        keycloakRepository.updateEnabledUser(
            "Bearer ${getMasterToken()}",
            userUuid,
            mapBody
        )
    }
    fun currentRolesInToken(token:String): MutableSet<String> {
        return getAccessTokenObject(token).realmAccess.roles
            ?: throw Exception("Couldnt be founded roles in token provided")


    }

//    fun assignRoleInKcToUserUuid(userUuid:String, roleUuid:String,roleName:String){
//        val mapRole: Map<String, String> = mapOf(
//            "id" to roleUuid,
//            "name" to roleName
//        )
//        keycloakRepository.addRoleToUser(
//            token = getMasterToken(),
//            userId = userUuid,
//            body = mapRole
//        )
//    }
    fun assignRoleInKcToUserUuid(userUuid:String, list: List<Map<String,String>>){

        try{
            keycloakRepository.addRoleToUser(
                token = "Bearer ${getMasterToken()}",
                userId = userUuid,
                body = list
            )
        }catch (ex:Exception){
            logger.error(ex.message)
        }
    }

    fun removeRoleInKcToUserUuid(userUuid:String, list: List<Map<String,String>>){

        try{
            keycloakRepository.removeRoleToUser(
                token = "Bearer ${getMasterToken()}",
                userId = userUuid,
                body = list
            )
        }catch (ex:Exception){
            logger.error(ex.message)
        }
    }
    fun createRoleInKc(name:String, description:String){
        try{
            val mapRole: Map<String, String> = mapOf(
                "name" to name,
                "description" to description
            )
            keycloakRepository.createRoleInRealm(
                token = "Bearer ${getMasterToken()}",
                body = mapRole
            )
        }catch (ex: Exception){
            logger.error("while adding role into real")
            logger.error(ex.message)
            //throw DuplicateRoleInRealmException("Double real in exception")
        }
    }

    fun getRoleInKc(name:String): RolesKcDto? {
        return keycloakRepository.getRoleDtoByRoleName(
            token = "Bearer ${getMasterToken()}",
            roleName = name )
    }
    fun getRolesByUserUuid(userUuid: String): List<RolesKcDto>{
        return keycloakRepository.getRolesOfUser(token = getMasterToken(), userUuid)
            ?: throw Exception("Null list of roles in Kc")
    }

    fun isThisRoleInCurrentToken(token: String, currentRole:String):Boolean{
        val rolesSet = currentRolesInToken(token)
        return rolesSet.contains(currentRole);
    }
}