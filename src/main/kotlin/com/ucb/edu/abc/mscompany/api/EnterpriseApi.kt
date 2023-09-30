package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.CompanyBl
import com.ucb.edu.abc.mscompany.dto.request.EnterpriseDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/config/enterprise")
class EnterpriseApi @Autowired constructor(

        private val companyBl: CompanyBl
) {

    @GetMapping("/{companyId}")
    fun getEnterprise(@PathVariable companyId: Int): ResponseDto<EnterpriseDto> {
            val enterpriseDto = companyBl.getCompanyById(companyId)
            return ResponseDto(enterpriseDto, "Request exitoso", true, "", )
    }


    @PutMapping("/{companyId}")
    fun updateEnterprise(@RequestBody enterpriseDto: EnterpriseDto,@PathVariable companyId: Int ): ResponseDto<String>{
            System.out.println("DTO recibido: $enterpriseDto")
            companyBl.updateCompany(enterpriseDto, companyId)
            return ResponseDto(
                    "",
                    "Request exitoso",
                    true,
                    "",
            )
    }

}
