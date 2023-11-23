package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.AccessPersonBl
import com.ucb.edu.abc.mscompany.bl.PermissionBl
import com.ucb.edu.abc.mscompany.bl.UserBl
import com.ucb.edu.abc.mscompany.dto.request.UserUpdateInfoDto
import com.ucb.edu.abc.mscompany.dto.response.AccessPersonWithImageDtoResponse
import com.ucb.edu.abc.mscompany.dto.response.PersonInfoDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletRequest
import kotlin.math.log

@RestController
@RequestMapping("/users")
class UserApi @Autowired constructor(
    private val userBl: UserBl,
    private val accessPersonBl: AccessPersonBl,
    private val permissionBl: PermissionBl
){

    private val logger = LoggerFactory.getLogger(this::class.java)
    @RequestMapping(value = [""], method = [RequestMethod.GET])
    fun getUserInfoSearched(@RequestParam("search") searched:String,
                  @RequestParam("limit") limit:Int,
                  ): ResponseEntity<ResponseDto<*>>
    {
        try{
            logger.info("Looking for user info going access person")
            return ResponseEntity(
                 ResponseDto(
                    data = accessPersonBl.getAccessPersonInfoAsUserByUsernameOrEmail(searched, limit),
                    message = null,
                    success = true,
                    errors = null
                    ),
                 HttpStatus.OK
            )
        }catch (ex: Exception){
            logger.error("Something wron while looking for user info ${ex.message}")
            return ResponseEntity(
                ResponseDto(
                    data = null,
                    message = "Algo salio terriblemente mal XD",
                    success = false,
                    errors = ex.message
                ),
                HttpStatus.BAD_REQUEST
            )
        }

    }
/*
 Sorry for this XD

 //@ModelAttribute updateInfo: UserUpdateInfoDto,  <---- this is the correct way but no hv idea y it doesn't work
    //@RequestBody updateInfo: UserUpdateInfoDto,
    //@RequestPart("country") country:String,
 */
    @RequestMapping(
        value = [""],
        method = [RequestMethod.POST])
    fun updateUserInfo(

    @RequestPart("imageProfile") imageProfileReq:MultipartFile?,
    @RequestPart("birthday") birthdayReq:String?,
    @RequestPart("names") namesReq:String?,
    @RequestPart("lastnames") lastnamesReq: String?,
    @RequestPart("gender") genderReq:String?,
    @RequestPart("address") addressReq:String?,
    @RequestPart("phoneNumber") phoneNumberReq:String?,
    @RequestPart("domainNumber") domainNumberReq:String?,
    @RequestPart("country") countryReq:String?,
    @RequestPart("dni") dniReq:String?,
    @RequestPart("dniExtension") dniExtensionReq:String?,
    @RequestHeader headers: Map<String, String>,
    ): ResponseEntity<ResponseDto<*>> {

        //logger.info("SOmething arrival $country")
        try{
            var updateInfo = UserUpdateInfoDto()
            updateInfo.apply {
                imageProfile = imageProfileReq
                birthday = birthdayReq
                names = namesReq
                lastnames =lastnamesReq
                gender = genderReq
                address = addressReq
                phoneNumber = phoneNumberReq
                domainNumber = domainNumberReq
                country = countryReq
                dni = dniReq
                dniExtension = dniExtensionReq
            }

            logger.info("Looking for user info going to update person")
            val userToken = headers["authorization"]!!.substring(7)
            userBl.updateUserInfo(userToken, updateInfo)
            return ResponseEntity(
                ResponseDto(
                    data = userBl.getUserInformationForGeneralApi(userToken),
                    message = null,
                    success = true,
                    errors = null
                ),
                HttpStatus.OK
            )
        }catch (ex: Exception){
            logger.error("Something wron while looking for user info ${ex.message}")
            return ResponseEntity(
                ResponseDto(
                    data = null,
                    message = "Algo salio terriblemente mal XD",
                    success = false,
                    errors = ex.message
                ),
                HttpStatus.BAD_REQUEST
            )
        }
    }

    @RequestMapping(value = ["/info"], method = [RequestMethod.GET])
    fun getUserInfo(
        @RequestHeader headers: Map<String, String>
    ): ResponseEntity<ResponseDto<*>>
    {
        try{
            val tokenAuth =  headers["authorization"]!!.substring(7)
            return ResponseEntity(
                ResponseDto(
                    data = userBl.getUserInformationForGeneralApi(tokenAuth),
                    message = null,
                    success = true,
                    errors = null
                ),
                HttpStatus.OK
            )
        }catch (ex: Exception){
            return ResponseEntity(
                ResponseDto(
                    data = null,
                    message = "Algo salio terriblemente mal XD",
                    success = false,
                    errors = ex.message
                ),
                HttpStatus.BAD_REQUEST
            )
        }

    }

    @RequestMapping(value = ["/invitations"], method = [RequestMethod.GET])
    fun getInvitationsByPerson(
        @RequestHeader headers: Map<String, String>
    ): ResponseEntity<ResponseDto<*>>
    {
        try{
            val tokenAuth =  headers["authorization"]!!.substring(7)
            return ResponseEntity(
                ResponseDto(
                    data = userBl.getPersonalInvitations(tokenAuth, null),
                    message = null,
                    success = true,
                    errors = null
                ),
                HttpStatus.OK
            )
        }catch (ex: Exception){
            return ResponseEntity(
                ResponseDto(
                    data = null,
                    message = "Algo salio terriblemente mal XD",
                    success = false,
                    errors = ex.message
                ),
                HttpStatus.BAD_REQUEST
            )
        }

    }

    @RequestMapping(value = ["/invitations/{id}"], method = [RequestMethod.GET])
    fun updateInvitation(
        @RequestHeader headers: Map<String, String>,
        @RequestParam("state") state: Boolean,
        @PathVariable id: Int,
    ): ResponseEntity<ResponseDto<*>>
    {
        try{
            val tokenAuth =  headers["authorization"]!!.substring(7)
            return ResponseEntity(
                ResponseDto(
                    data = permissionBl.updatePersonalUpdatedInvitations(tokenAuth, id, state ),
                    message = null,
                    success = true,
                    errors = null
                ),
                HttpStatus.OK
            )
        }catch (ex: Exception){
            return ResponseEntity(
                ResponseDto(
                    data = null,
                    message = "Algo salio terriblemente mal XD",
                    success = false,
                    errors = ex.message
                ),
                HttpStatus.BAD_REQUEST
            )
        }

    }

}