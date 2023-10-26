package com.ucb.edu.abc.mscompany.api

import org.springframework.web.bind.annotation.RestController

import com.ucb.edu.abc.mscompany.bl.JournalBl
import com.ucb.edu.abc.mscompany.bl.LedgerBl
import com.ucb.edu.abc.mscompany.bl.TransactionBl
import com.ucb.edu.abc.mscompany.dto.request.JournalRequestDto
import com.ucb.edu.abc.mscompany.dto.request.LedgerRequestDto
import com.ucb.edu.abc.mscompany.dto.request.TransactionDto
import com.ucb.edu.abc.mscompany.dto.response.JournalResponseDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import com.ucb.edu.abc.mscompany.dto.response.LedgerResponseDto

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/mayor/book")
class LedgerApi @Autowired constructor(
        private val ledgerBl: LedgerBl
)
{
    /*@PostMapping("/{companyId}")
    fun getLedger(@PathVariable companyId: Int, @RequestBody ledgerRequestDto: LedgerRequestDto): ResponseEntity<ResponseDto<LedgerResponseDto>>{
        val journalResponseDto = ledgerBl.getLedger(companyId, ledgerRequestDto)

        return try {
            ResponseEntity.ok(
                    ResponseDto(journalResponseDto, "Libro Mayor obtenido con exito", true, "" ))

        }catch (e: Exception){
            ResponseEntity.ok(
                    ResponseDto(null, "Error al obtener el libro mayor", false, e.toString() ))

        }
    }*/

}