package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.FileBl
import com.ucb.edu.abc.mscompany.bl.JournalBl
import com.ucb.edu.abc.mscompany.bl.PDFTurtleBl
import com.ucb.edu.abc.mscompany.bl.ReportBl
import com.ucb.edu.abc.mscompany.dto.request.JournalRequestDto
import com.ucb.edu.abc.mscompany.dto.response.JournalResponseDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/diary/book")
class JournalApi @Autowired constructor(
    private val journalBl: JournalBl,
    private val pdfTurtleBl: PDFTurtleBl,
    private val fileBl: FileBl,
    private val reportBl: ReportBl
){
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/{companyId}")
    fun getJournal(
        @PathVariable companyId: Int,
        @RequestBody journalRequestDto: JournalRequestDto,
        @RequestHeader headers: Map<String, String>): ResponseEntity<ResponseDto<JournalResponseDto>>{
        val journalResponseDto = journalBl.getJournal(companyId, journalRequestDto)
        val reportEntity = reportBl.factoryReportEntity(headers, companyId, "", "EXCEL", "Libro Diario")
        reportBl.createReport(reportEntity)
        return ResponseEntity.ok(
            ResponseDto(journalResponseDto, "Datos obtenidos con exito", true, "" ))
    }

    @PostMapping("/pdf/{companyId}")
    suspend fun getJournalPDF(
        @PathVariable companyId: Int,
        @RequestBody journalRequestDto: JournalRequestDto,
        @RequestHeader headers: Map<String, String>): ResponseEntity<ResponseDto<String>>{
        logger.info("Journal Request: $journalRequestDto")
        val journalResponseDto = journalBl.getJournalForPDF(companyId, journalRequestDto)
        val footer = pdfTurtleBl.readHtmlToString("ReportPDF/libro-diario/footer.html")
        val header = pdfTurtleBl.readHtmlToString("ReportPDF/libro-diario/header.html")
        val body = pdfTurtleBl.readHtmlToString("ReportPDF/libro-diario/index.html")
        val pdf = pdfTurtleBl.getPDF(footer, header, body, journalResponseDto)
        val url = fileBl.generatePDFFile(pdf!!, "application/pdf", companyId, headers, "Libro Diario" )
        return ResponseEntity.ok(
            ResponseDto(url, "Reporte obtenido con exito", true, "" ))
    }




}