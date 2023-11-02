package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.JournalBl
import com.ucb.edu.abc.mscompany.bl.PDFTurtleBl
import com.ucb.edu.abc.mscompany.dto.request.JournalRequestDto
import com.ucb.edu.abc.mscompany.dto.response.JournalResponseDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.nio.charset.Charset

@RestController
@RequestMapping("/diary/book")
class JournalApi @Autowired constructor(
    private val journalBl: JournalBl,
    private val pdfTurtleBl: PDFTurtleBl
){
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

    @PostMapping("/pdf/{companyId}")
    suspend fun getJournalPDF(@PathVariable companyId: Int, @RequestBody journalRequestDto: JournalRequestDto): ResponseEntity<ResponseDto<ByteArray>>{
        val journalResponseDto = journalBl.getJournal(companyId, journalRequestDto)
        val footer = pdfTurtleBl.readHtmlToString("ReportPDF/libro-diario/footer.html")
        val header = pdfTurtleBl.readHtmlToString("ReportPDF/libro-diario/header.html")
        val body = pdfTurtleBl.readHtmlToString("ReportPDF/libro-diario/index.html")
        val pdf = pdfTurtleBl.getPDF(header, body, footer, journalResponseDto)
        return ResponseEntity.ok(
            ResponseDto(pdf, "Datos obtenidos con exito", true, "" ))
    }




}