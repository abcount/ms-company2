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
    fun handleFileUpload(request: HttpServletRequest): ResponseEntity<String> {
        val multipartRequest = request as? MultipartHttpServletRequest
                ?: return ResponseEntity.badRequest().body("Request no es MultipartHttpServletRequest")

        val jsonStr = multipartRequest.getParameter("datos")
        if (jsonStr == null) {
            return ResponseEntity.badRequest().body("No se ha recibido JSON en el parámetro 'datos'")
        }

        val createCompanyDto: CreateCompanyDto
        try {
            createCompanyDto = objectMapper.readValue(jsonStr)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body("Error al convertir JSON a CreateCompanyDto: ${e.message}")
        }

        println("DTO recibido: $createCompanyDto")

        val file = multipartRequest.getFile("image")
        if (file == null) {
            return ResponseEntity.badRequest().body("No se ha recibido el archivo en el parámetro 'image'")
        }

        val companyId = configCompany.createCompany(createCompanyDto,"", file)
        return ResponseEntity.ok("Se ha creado la compañia con ID: $companyId")
    }

}