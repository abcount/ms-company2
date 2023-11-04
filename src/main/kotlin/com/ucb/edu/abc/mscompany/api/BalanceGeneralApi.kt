package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.BalanceGeneralBl
import com.ucb.edu.abc.mscompany.dto.request.BalanceGeneralRequestDto
import com.ucb.edu.abc.mscompany.dto.response.BalanceGeneralResponseDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/general/balance")
class BalanceGeneralApi @Autowired constructor
(private val balanceGeneralBl: BalanceGeneralBl)
{

   @PostMapping("/{companyId}")
    fun getBalanceGeneral(@PathVariable companyId: Int, @RequestBody balanceGeneralRequestDto: BalanceGeneralRequestDto): ResponseEntity<ResponseDto<BalanceGeneralResponseDto>> {
        val balanceGeneralResponseDto = balanceGeneralBl.getBalanceGeneral(companyId, balanceGeneralRequestDto)

        return try {
            ResponseEntity.ok(
                    ResponseDto(balanceGeneralResponseDto, "Datos obtenidos con exito", true, "" ))

        }catch (e: Exception){
            ResponseEntity.ok(
                    ResponseDto(null, "Error al obtener el balance general", false, e.toString() ))

        }
    }



}
