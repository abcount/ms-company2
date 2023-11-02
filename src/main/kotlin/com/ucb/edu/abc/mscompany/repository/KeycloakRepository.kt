package com.ucb.edu.abc.mscompany.repository

import com.ucb.edu.abc.mscompany.config.ConfigFeignClient
import com.ucb.edu.abc.mscompany.dto.response.KeycloakTokenDto
import com.ucb.edu.abc.mscompany.dto.response.KeycloakUserDto
import com.ucb.edu.abc.mscompany.dto.response.RolesKcDto
import feign.Headers
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@FeignClient(name = "keycloak",
    url = "\${keycloak.auth-server-url}",
    configuration = [ConfigFeignClient::class] )

interface KeycloakRepository {
    /*
        client_id: admin-cli
        username: <admin>
        password: <admin>
        grant_type: password
     */
    @PostMapping(value = ["/realms/master/protocol/openid-connect/token"], consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun getMasterToken(@RequestBody body: Map<String, *>): KeycloakTokenDto


    @GetMapping(value= ["/admin/realms/\${keycloak.realm}/users/{user-id}"])
    fun getUserInformation(@RequestHeader("Authorization") token: String,
                           @PathVariable(value = "user-id") userId: String): KeycloakUserDto

    @PutMapping(value = ["/admin/realms/\${keycloak.realm}/users/{user-id}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateEnabledUser(@RequestHeader("Authorization") token: String,
                          @PathVariable(value = "user-id") userId: String,
                          @RequestBody body:Map<String, *>)

    @PutMapping(value = ["/admin/realms/\${keycloak.realm}/users/{user-id}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateUserInfo(@RequestHeader("Authorization") token: String,
                       @PathVariable(value = "user-id") userId: String,
                       @RequestBody body:Map<String, *>)

    /*
        REQUEST BODY:
        { "type": "password",
         "temporary": false,
         "value": "NEW_PASSWORD"
        }
         */
    @PutMapping(value = ["/admin/realms/\${keycloak.realm}/users/{user-id}/reset-password"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun changePasswordOfUser(@RequestHeader("Authorization") token: String,
                             @PathVariable(value = "user-id") userId: String,
                             @RequestBody body:Map<String, *>)

    /*
    RESPONSE:
    [
        {
            "id": "0e97f743-07a2-4dfa-90de-66cdbe9cf0fc",
            "name": "default-roles-testing2",
            "description": "${role_default-roles}",
            "composite": true,
            "clientRole": false,
            "containerId": "bb1c7a14-c003-455c-ba76-be14ba5a4205"
        },
        {
            "id": "12e5d04f-6e48-41db-9a6e-b36c2543050c",
            "name": "USER",
            "description": "",
            "composite": false,
            "clientRole": false,
            "containerId": "bb1c7a14-c003-455c-ba76-be14ba5a4205"
        }
    ]
     */
    @GetMapping(value = ["/admin/realms/\${keycloak.realm}/users/{user-id}/role-mappings/realm"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getRolesOfUser(@RequestHeader("Authorization") token: String,
                       @PathVariable(value = "user-id") userId: String):List<RolesKcDto>?


    /*
    RESPONSE: 204 NO content
    REQUEST BODY:

    [
        {
            "id": "0e97f743-07a2-4dfa-90de-66cdbe9cf0fc",
            "name": "default-roles-testing2"
        }
    ]
     */
    @DeleteMapping(value = ["/admin/realms/\${keycloak.realm}/users/{user-id}/role-mappings/realm"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteRolesOfUser(@RequestHeader("Authorization") token: String,
                          @PathVariable(value = "user-id") userId: String,
                          @RequestBody body:List<Map<String, *>>)

    /*
    RESPONSE 204 NO CONTENT
    REQUEST BODY:
    [
        {
            "id": "0e97f743-07a2-4dfa-90de-66cdbe9cf0fc",
            "name": "default-roles-testing2"
        }
    ]
     */
    @PostMapping(value = ["/admin/realms/\${keycloak.realm}/users/{user-id}/role-mappings/realm"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addRoleToUser(@RequestHeader("Authorization") token: String,
                      @PathVariable(value = "user-id") userId: String,
                      @RequestBody body:List<Map<String, *>>)

    @DeleteMapping(value = ["/admin/realms/\${keycloak.realm}/users/{user-id}/role-mappings/realm"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun removeRoleToUser(@RequestHeader("Authorization") token: String,
                      @PathVariable(value = "user-id") userId: String,
                      @RequestBody body:List<Map<String, *>>)

    @GetMapping(value = ["/admin/realms/\${keycloak.realm}/roles/{roleName}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getRoleDtoByRoleName(@RequestHeader("Authorization") token: String,
                             @PathVariable(value = "roleName") roleName:String): RolesKcDto?

    /*
        {
		    "name": "NO_NAME",
		    "description": "${NO_NAME}"
	    }
     */
    @PostMapping(value = ["/admin/realms/\${keycloak.realm}/roles"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createRoleInRealm(@RequestHeader("Authorization") token: String,
                          @RequestBody body:Map<String, *>)

}