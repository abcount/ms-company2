package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.AccessPersonBl
import com.ucb.edu.abc.mscompany.bl.UserBl
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.math.log

@RestController
@RequestMapping("/users")
class UserApi @Autowired constructor(
    private val userBl: UserBl,
    private val accessPersonBl: AccessPersonBl
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

    @RequestMapping(value = ["/info"], method = [RequestMethod.GET])
    fun getUserInfo(
        @RequestHeader headers: Map<String, String>
    ): ResponseEntity<ResponseDto<*>>
    {
        try{
            val tokenAuth =  headers["authorization"]!!.substring(7)
            return ResponseEntity(
                ResponseDto(
                    data = userBl.getUserInformationByToken(tokenAuth),
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