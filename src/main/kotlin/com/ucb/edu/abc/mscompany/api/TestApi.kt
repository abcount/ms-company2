package com.ucb.edu.abc.mscompany.api


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ucb.edu.abc.mscompany.bl.CompanyBl
import com.ucb.edu.abc.mscompany.bl.RoleBl
import com.ucb.edu.abc.mscompany.bl.UserBl
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import com.ucb.edu.abc.mscompany.entity.UserEntity
import com.ucb.edu.abc.mscompany.enums.UserAbcCategory
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource


@RestController
@RequestMapping("/test-api")
class TestApi @Autowired constructor(
    private val companyBl: CompanyBl,
    private val userBl: UserBl,
    private val rolesBl: RoleBl
) {
    val objectMapper = jacksonObjectMapper()


    @RequestMapping(value = ["/3/{id}"], method = [RequestMethod.GET])
    fun getImage3(@PathVariable id: Int): ResponseEntity<ByteArray> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.IMAGE_JPEG
        val base64 = companyBl.getImageOfCompany(id)
        return ResponseEntity(base64, headers, HttpStatus.OK)
    }

    @RequestMapping(value = ["/company/{id}"] , method = [RequestMethod.GET])
    fun getUserByCompanyAndToken(@PathVariable id:Int, @RequestHeader headers: Map<String, String>): ResponseDto<*> {
        val tokenAuth =  headers["authorization"]!!.substring(7)

        val ac=  userBl.getUserIdByCompanyIdAndToken(
            token = tokenAuth,
            companyId = id,
            userAbcCategory = UserAbcCategory.ACTIVE,
            currentAccessPersonEntity = null
        )
        return ResponseDto(
            data = ac,
            message = null,
            success = true,
            errors = null
        )
    }
    @RequestMapping(value = ["/all-roles"], method = [RequestMethod.GET])
    fun getALl(): Any{
        return rolesBl.getAllRolesFromEnum();
    }

    @RequestMapping(value = ["/company/{companyId}/users/{userId}/roles"], method = [RequestMethod.GET])
    fun getPermissionsAndRolesByCompanyIdAndUserId(
        @PathVariable companyId:Int,
        @PathVariable userId:Int
    ):Any{
        return companyBl.getPermissionAndRolesByUserAndCompany(userId, companyId);
    }
}