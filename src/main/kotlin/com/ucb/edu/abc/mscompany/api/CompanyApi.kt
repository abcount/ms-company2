package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.ConfigCompany
import com.ucb.edu.abc.mscompany.dto.request.CreateCompanyDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/ms-company")
class CompanyApi @Autowired constructor(
        private val configCompany: ConfigCompany

){
    @PostMapping("/company")
    fun createCompany(
        @RequestBody body: CreateCompanyDto,
        @RequestHeader headers: Map<String, String>
    ): ResponseEntity<ResponseDto<Int>>{
        //val tokenAuth = headers["authorization"]!!.substring(7)
        val companyId = configCompany.createCompany(body,"")
        val responseDto = ResponseDto<Int>(
                companyId, "Se creo la compañia correctamente. CompanyId:$companyId", true, null)
        return ResponseEntity.ok(responseDto)
    }
}