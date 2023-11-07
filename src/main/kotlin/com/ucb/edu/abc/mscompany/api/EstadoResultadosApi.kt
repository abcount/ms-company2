package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.EstadoResultadosBl
import com.ucb.edu.abc.mscompany.dto.request.BalanceGeneralRequestDto
import com.ucb.edu.abc.mscompany.dto.request.EstadoResultadosRequestDto
import com.ucb.edu.abc.mscompany.dto.response.BalanceGeneralResponseDto
import com.ucb.edu.abc.mscompany.dto.response.EstadoResultadosResponseDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/general/estado-resultados")
class EstadoResultadosApi @Autowired constructor(
        private val estadoResultadosBl: EstadoResultadosBl
) {
    @PostMapping("/{companyId}")
    fun getEstadoResultados(@PathVariable companyId: Int, @RequestBody estadoResultadosRequestDto: EstadoResultadosRequestDto): ResponseEntity<ResponseDto<EstadoResultadosResponseDto>> {
        val resultStateResponseDto = estadoResultadosBl.getEstadoResultados(companyId, estadoResultadosRequestDto)

        return try {
            ResponseEntity.ok(
                    ResponseDto(resultStateResponseDto, "Datos obtenidos con exito", true, "" ))

        }catch (e: Exception){
            ResponseEntity.ok(
                    ResponseDto(null, "Error al obtener el estado de resultados", false, e.toString() ))

        }
    }


}