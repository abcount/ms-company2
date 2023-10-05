package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.ConfigCompany
import com.ucb.edu.abc.mscompany.dto.request.CreateCompanyDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import java.util.*
import javax.servlet.http.HttpServletRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

@RestController
@RequestMapping("/api/v1/ms-company")
class CompanyApi @Autowired constructor(
        private val configCompany: ConfigCompany

){
    val objectMapper = jacksonObjectMapper()
    @PostMapping("/company")
    fun handleFileUpload(request: HttpServletRequest): ResponseDto<String> {
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

        val companyId = configCompany.createCompany(createCompanyDto,"", file)
        response.data = "Se ha creado la compañia con el id: $companyId"
        response.message = "Se ha creado la compañia con el id: $companyId"
        response.success = true
        response.errors = ""
        return response
    }

}