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

@RestController
@RequestMapping("/api/v1/ms-company")
class CompanyApi @Autowired constructor(
        private val configCompany: ConfigCompany

){
    @PostMapping("/company")
    fun createCompany(
        @RequestParam image: MultipartFile,
        @RequestParam datos: CreateCompanyDto,
        @RequestHeader headers: Map<String, String>
    ): ResponseEntity<ResponseDto<Int>>{
        //val tokenAuth = headers["authorization"]!!.substring(7)
        val companyId = configCompany.createCompany(datos,"", image)
        val responseDto = ResponseDto<Int>(
                companyId, "Se creo la compañia correctamente. CompanyId:$companyId", true, null)
        return ResponseEntity.ok(responseDto)
    }
}