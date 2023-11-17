package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.ExchangeRateBl
import com.ucb.edu.abc.mscompany.dto.request.CreateExchangeRatDto
import com.ucb.edu.abc.mscompany.dto.request.ExchangeDto
import com.ucb.edu.abc.mscompany.dto.request.UpdateExchangeRate
import com.ucb.edu.abc.mscompany.dto.response.ListExchangeRateDateDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import com.ucb.edu.abc.mscompany.entity.ExchangeRateEntity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/exchangeRate")
class ExchangeRateApi @Autowired constructor(
    private val exchangeRateBl: ExchangeRateBl
){

    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/exist/{companyId}")
    fun existRegister(
            @PathVariable companyId: Int,
            @RequestParam(required = false) date: String?): ResponseEntity<ResponseDto<Boolean>>{
        logger.info("Verificando si existen registros de tipos de cambio")
        var data = false

        if(date != null){
            val newDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            data = exchangeRateBl.existRegisterByDay(companyId, newDate)
        }else{
            data = exchangeRateBl.existRegister(companyId)
        }
        val message =  if(data) "Ya existen registros de tipos de cambio el día $date" else "No existen registros de tipos de cambio el día $date"
        return ResponseEntity.ok(
            ResponseDto(data, message, true, "" ))
    }

    @PostMapping("/{companyId}")
    fun createExchangeRate(@PathVariable companyId: Int, @RequestBody createExchangeRatDto: CreateExchangeRatDto): ResponseEntity<ResponseDto<*>>{
        logger.info("Creando tipos de cambio")
        val exist = exchangeRateBl.existRegisterByDay(companyId, createExchangeRatDto.date)
        if(exist) {
            return ResponseEntity.ok(
                ResponseDto(false, "Ya existen registros de tipos de cambio el día de hoy", false, ""))
        }
        exchangeRateBl.createExchangeRateList(createExchangeRatDto.exchange, companyId, createExchangeRatDto.date)
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

    @PutMapping("")
    fun updateExchangeRate(@RequestBody listExchangeRate: List<UpdateExchangeRate>): ResponseEntity<ResponseDto<String>>{
        logger.info("Actualizando tipos de cambio")
        exchangeRateBl.updateListExchangeRate(listExchangeRate)
        return ResponseEntity.ok(
            ResponseDto("", "Tipo de cambio actualizado correctamente", true, "" ))
    }






}