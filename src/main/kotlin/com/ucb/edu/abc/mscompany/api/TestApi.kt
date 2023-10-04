package com.ucb.edu.abc.mscompany.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ucb.edu.abc.mscompany.dto.request.CreateCompanyDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartHttpServletRequest
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/test-api")
class TestApi {
    val objectMapper = jacksonObjectMapper()
    @PostMapping("/company")
    fun handleFileUpload(request: HttpServletRequest): ResponseDto<String> {
        val multipartRequest = request as? MultipartHttpServletRequest
            ?: return ResponseDto("No se ha recibido un archivo 1","",false,"")

        val jsonStr = multipartRequest.getParameter("datos")
        val response = ResponseDto<String>("", "", false, "")

        if (jsonStr == null) {
            return ResponseDto("No se ha recibido un archivo 2","",false,"")
        }

        val createCompanyDto: CreateCompanyDto
        try {
            createCompanyDto = objectMapper.readValue(jsonStr)
        } catch (e: Exception) {
            return ResponseDto("No se ha recibido un archivo 3",e.message,false,"")
        }

        println("DTO recibido: $createCompanyDto")

        val file = multipartRequest.getFile("image")
        if (file == null) {
            return ResponseDto("No se ha recibido un archivo 4","",false,"")
        }

        return ResponseDto("A", "", false, "")
    }
}