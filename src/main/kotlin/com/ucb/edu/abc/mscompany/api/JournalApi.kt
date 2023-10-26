package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.JournalBl
import com.ucb.edu.abc.mscompany.dto.request.JournalRequestDto
import com.ucb.edu.abc.mscompany.dto.response.JournalResponseDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/diary/book")
class JournalApi @Autowired constructor
(private val journalBl: JournalBl)
{
    @PostMapping("/{companyId}")
    fun getJournal(@PathVariable companyId: Int, @RequestBody journalRequestDto: JournalRequestDto): ResponseEntity<ResponseDto<JournalResponseDto>>{
        val journalResponseDto = journalBl.getJournal(companyId, journalRequestDto)

        return try {
            ResponseEntity.ok(
                    ResponseDto(journalResponseDto, "Datos obtenidos con exito", true, "" ))

        }catch (e: Exception){
            ResponseEntity.ok(
                    ResponseDto(null, "Error al obtener el libro diario", false, e.toString() ))

        }
    }
}