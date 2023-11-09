package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.SumasSaldosBl
import com.ucb.edu.abc.mscompany.dto.request.SumasSaldosRequestDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import com.ucb.edu.abc.mscompany.dto.response.SumasSaldosResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/sumas/saldos")
class SumasSaldosApi @Autowired constructor(
    private val sumasSaldosBl: SumasSaldosBl
){
    @PostMapping("/{companyId}")
    fun getSumasSaldos(
        @PathVariable companyId: Int,
        @RequestBody sumasSaldosRequestDto: SumasSaldosRequestDto,
        @RequestHeader headers: Map<String,String>): ResponseEntity<ResponseDto<SumasSaldosResponseDto>> {
        val sumasSaldosResponseDto = sumasSaldosBl.getSumasSaldos(companyId, sumasSaldosRequestDto)
        return ResponseEntity.ok(ResponseDto(sumasSaldosResponseDto, "Sumas y saldos obtenidos con exito", true, "" ))
    }
}