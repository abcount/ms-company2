package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.CompanyBl
import com.ucb.edu.abc.mscompany.bl.ExchangeBl
import com.ucb.edu.abc.mscompany.bl.ExchangeMoneyBl
import com.ucb.edu.abc.mscompany.dto.request.EnterpriseDto
import com.ucb.edu.abc.mscompany.dto.response.Currency
import com.ucb.edu.abc.mscompany.dto.response.EnterpriseCurrencyDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import com.ucb.edu.abc.mscompany.entity.ExchangeEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/config/enterprise")
class EnterpriseApi @Autowired constructor(

        private val companyBl: CompanyBl,
        private val exchangeMoneyBl: ExchangeMoneyBl,
        private val exchangeBl: ExchangeBl
) {
    /*
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
    */
    @GetMapping("/currency/{companyId}")
    fun getCurrencyConfig(@PathVariable companyId: Int): ResponseEntity<ResponseDto<EnterpriseCurrencyDto>> {
            val currency = exchangeMoneyBl.getAllCurrenciesByCompanyId(companyId).map { Currency( it.abbreviationName, it.moneyName) }
            val company = companyBl.get(companyId)
            var enterpriseCurrencyDto = EnterpriseCurrencyDto(currency, company.openingDate.toString())
            val responseDto = ResponseDto(enterpriseCurrencyDto, "Datos obtenidos con exito", true, "")
            return ResponseEntity.ok(responseDto)
    }

    @GetMapping("/currency")
    fun getExchangeByNameOrIso(@RequestParam(required = false) name: String?): ResponseEntity<ResponseDto<List<ExchangeEntity>>> {
        if(name != null){
            val exchanges = exchangeBl.getExchangeByNameOrIso(name)
            val responseDto = ResponseDto<List<ExchangeEntity>>(
                    exchanges,
                    "Se obtuvieron los tipos de cambio con exito",
                    true,
                    ""
            )
            return ResponseEntity.ok(responseDto)
        }
        return ResponseEntity.badRequest().body(ResponseDto<List<ExchangeEntity>>(
                null,
                "No se pudo obtener los tipos de cambio",
                false,
                "No se recibio el parametro name"
        ))
    }

    /*@PostMapping("/currency/")
    fun createCurrency()*/



}
