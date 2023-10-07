package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.ConfigCompany
import com.ucb.edu.abc.mscompany.dto.request.CreateCompanyDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import java.util.*
import javax.servlet.http.HttpServletRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ucb.edu.abc.mscompany.bl.CompanyBl
import com.ucb.edu.abc.mscompany.bl.UserBl
import com.ucb.edu.abc.mscompany.dto.response.CompanyListDto
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/ms-company")
class CompanyApi @Autowired constructor(
        private val configCompany: ConfigCompany,
    private val companyBl: CompanyBl,
    private val userBl: UserBl
){
    val objectMapper = jacksonObjectMapper()
    @PostMapping("/company")
    fun handleFileUpload(request: HttpServletRequest, @RequestHeader headers: Map<String, String>): ResponseDto<String> {
        val multipartRequest = request as? MultipartHttpServletRequest
                ?: return ResponseDto("No se ha recibido un archivo","",false,"")

        val jsonStr = multipartRequest.getParameter("datos")
        val response = ResponseDto<String>("", "", false, "")

        if (jsonStr == null) {
            return ResponseDto("No se ha recibido un archivo","",false,"")
        }

        val createCompanyDto: CreateCompanyDto
        try {
            createCompanyDto = objectMapper.readValue(jsonStr)
        } catch (e: Exception) {
            return ResponseDto("No se ha recibido un archivo","",false,"")
        }


        val file = multipartRequest.getFile("image")
        if (file == null) {
            return ResponseDto("No se ha recibido un archivo","",false,"")
        }
        //val token = ""// token in header, if not use please uncomment this line
        val token = headers["authorization"]!!.substring(7)
        val companyId = configCompany.createCompany(createCompanyDto,token, file)
        response.data = "Se ha creado la compañia con el id: $companyId"
        response.message = "Se ha creado la compañia con el id: $companyId"
        response.success = true
        response.errors = ""
        return response
    }

    @RequestMapping(value = ["/companies"], method = [RequestMethod.GET])
    fun getImageByCompanyId( @RequestHeader headers: Map<String, String>): ResponseEntity<ResponseDto<*>> {

        try{
            val tokenAuth =  headers["authorization"]!!.substring(7)
            val listResult = companyBl.getCompaniesByAccessPerson(tokenAuth)
            return ResponseEntity(
                ResponseDto<List<CompanyListDto>>(
                    data = listResult,
                    message = null,
                    success = true,
                    errors = null
                ),
                HttpStatus.OK
            )
        }catch (ex: Exception){
            println(ex.message)
            return ResponseEntity(
                ResponseDto<List<CompanyListDto>>(
                    data = null,
                    message = ex.message,
                    success = false,
                    errors = "CODE: nnnn"
                ),
                HttpStatus.BAD_REQUEST
            )
        }
    }

    @RequestMapping(value = ["/companies/{id}/employees"], method = [RequestMethod.GET])
    fun getUsersAndInvited(@PathVariable id: Int): ResponseEntity<ResponseDto<*>> {
        try{
            val invitationAndUsers = userBl.getUserInformationByCompanyId(companyId = id)
            return ResponseEntity(
                ResponseDto(
                    data = invitationAndUsers,
                    message = null,
                    success = true,
                    errors = null
                ),
                HttpStatus.OK
            )
        }catch (ex: Exception){
            println(ex.message)
            return ResponseEntity(
                ResponseDto<List<CompanyListDto>>(
                    data = null,
                    message = ex.message,
                    success = false,
                    errors = "CODES: 0001, 0002"
                ),
                HttpStatus.BAD_REQUEST
            )
        }
    }

}