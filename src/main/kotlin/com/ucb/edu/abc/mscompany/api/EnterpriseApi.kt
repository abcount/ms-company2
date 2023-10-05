package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.CompanyBl
import com.ucb.edu.abc.mscompany.bl.ExchangeMoneyBl
import com.ucb.edu.abc.mscompany.dto.request.EnterpriseDto
import com.ucb.edu.abc.mscompany.dto.response.Currency
import com.ucb.edu.abc.mscompany.dto.response.EnterpriseCurrencyDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/config/enterprise")
class EnterpriseApi @Autowired constructor(

        private val companyBl: CompanyBl,
        private val exchangeMoneyBl: ExchangeMoneyBl
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

    @GetMapping("/currency/{companyId}")
    fun getCurrencyConfig(@PathVariable companyId: Int): ResponseEntity<ResponseDto<EnterpriseCurrencyDto>> {
            val currency = exchangeMoneyBl.getAllCurrenciesByCompanyId(companyId).map { Currency( it.abbreviationName, it.moneyName) }
            val company = companyBl.get(companyId)
            var enterpriseCurrencyDto = EnterpriseCurrencyDto(currency, company.openingDate.toString())
            val responseDto = ResponseDto(enterpriseCurrencyDto, "Datos obtenidos con exito", true, "")
            return ResponseEntity.ok(responseDto)
    }

    /*@PostMapping("/currency/")
    fun createCurrency()*/

}
