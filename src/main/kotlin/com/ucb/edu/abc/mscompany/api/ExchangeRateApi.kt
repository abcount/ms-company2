package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.ExchangeRateBl
import com.ucb.edu.abc.mscompany.dto.request.ExchangeDto
import com.ucb.edu.abc.mscompany.dto.response.ListExchangeRateDateDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import com.ucb.edu.abc.mscompany.entity.ExchangeRateEntity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/exchangeRate")
class ExchangeRateApi @Autowired constructor(
    private val exchangeRateBl: ExchangeRateBl
){

    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/exist/{companyId}")
    fun existRegister(@PathVariable companyId: Int): ResponseEntity<ResponseDto<Boolean>>{
        logger.info("Verificando si existen registros de tipos de cambio")
        val exist = exchangeRateBl.existRegister(companyId)
        return ResponseEntity.ok(
            ResponseDto(exist, "Datos obtenidos con exito", true, "" ))
    }

    @PostMapping("/{companyId}")
    fun createExchangeRate(@PathVariable companyId: Int, @RequestBody exchangeRateDto: List<ExchangeDto>): ResponseEntity<ResponseDto<*>>{
        logger.info("Creando tipos de cambio")
        val exist = exchangeRateBl.existRegister(companyId)
        if(exist) {
            return ResponseEntity.ok(
                ResponseDto(false, "Ya existen registros de tipos de cambio el día de hoy", false, ""))
        }
        exchangeRateBl.createExchangeRateList(exchangeRateDto, companyId)
        return ResponseEntity.ok(
            ResponseDto("", "Tipo de cambio creado correctamente", true, "" ))
    }

    @GetMapping("/{companyId}")
    fun getAllExchangeRate(@PathVariable companyId: Int): ResponseEntity<ResponseDto<List<ExchangeRateEntity>>>{
        logger.info("Obteniendo tipos de cambio")
        val listExchange = exchangeRateBl.getAllExchangeRateByCompanyId(companyId)
        return ResponseEntity.ok(
            ResponseDto(listExchange, "Datos obtenidos con exito", true, "" ))
    }

    @GetMapping("/list/{companyId}")
    fun getAllExchangeRateGroupByDate(@PathVariable companyId: Int): ResponseEntity<ResponseDto<ListExchangeRateDateDto>>{
        logger.info("Obteniendo tipos de cambio")
        val listExchange = exchangeRateBl.getListExchangeRateGroupByDate(companyId)
        return ResponseEntity.ok(
            ResponseDto(listExchange, "Datos obtenidos con exito", true, "" ))
    }





}