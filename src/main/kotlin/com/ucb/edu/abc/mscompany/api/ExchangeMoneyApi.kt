package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.ExchangeMoneyBl
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import com.ucb.edu.abc.mscompany.entity.ExchangeMoneyEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/exchangeMoney")
class ExchangeMoneyApi @Autowired constructor(
        private val exchangeMoneyBl: ExchangeMoneyBl
){

    @GetMapping("/{companyId}")
    fun getAllExchangeMoney(@PathVariable companyId: Int): ResponseEntity<ResponseDto<List<ExchangeMoneyEntity>>>{
        val listExchange = exchangeMoneyBl.getAllCurrenciesByCompanyId(companyId)
        return ResponseEntity.ok(
            ResponseDto(listExchange, "Datos obtenidos con exito", true, "" ))
    }



}