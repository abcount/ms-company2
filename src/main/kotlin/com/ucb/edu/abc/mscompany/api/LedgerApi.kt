package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.*
import org.springframework.web.bind.annotation.RestController

import com.ucb.edu.abc.mscompany.dto.request.JournalRequestDto
import com.ucb.edu.abc.mscompany.dto.request.LedgerRequestDto
import com.ucb.edu.abc.mscompany.dto.request.TransactionDto
import com.ucb.edu.abc.mscompany.dto.response.JournalResponseDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import com.ucb.edu.abc.mscompany.dto.response.LedgerResponseDto
import com.ucb.edu.abc.mscompany.entity.ReportEntity
import org.apache.coyote.Response

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/mayor/book")
class LedgerApi @Autowired constructor(
        private val ledgerBl: LedgerBl,
        private val reportBl: ReportBl,
        private val pdfTurtleBl: PDFTurtleBl,
        private val fileBl: FileBl
)
{
    @PostMapping("/{companyId}")
    fun getLedger(
        @PathVariable companyId: Int,
        @RequestBody ledgerRequestDto: LedgerRequestDto,
        @RequestHeader headers: Map<String,String>): ResponseEntity<ResponseDto<LedgerResponseDto>>{
        val journalResponseDto = ledgerBl.getLedger(companyId, ledgerRequestDto)
        val reportEntity = reportBl.factoryReportEntity(headers, companyId, "", "EXCEL", "Libro Mayor")
        reportBl.createReport(reportEntity)
        return ResponseEntity.ok(
            ResponseDto(journalResponseDto, "Libro Mayor obtenido con exito", true, "" ))
    }

    @PostMapping("/pdf/{companyId}")
    suspend fun getLedgerPdf(
        @PathVariable companyId: Int,
        @RequestBody ledgerRequestDto: LedgerRequestDto,
        @RequestHeader headers: Map<String,String>): ResponseEntity<ResponseDto<String>>{
        val journalResponseDto = ledgerBl.getLedgerPdf(companyId, ledgerRequestDto, headers)

        val footer = pdfTurtleBl.readHtmlToString("ReportPDF/libro-mayor/footer.html")
        val header = pdfTurtleBl.readHtmlToString("ReportPDF/libro-mayor/header.html")
        val body = pdfTurtleBl.readHtmlToString("ReportPDF/libro-mayor/index.html")

        val pdf = pdfTurtleBl.getPDF(footer, header, body, journalResponseDto)
        val url = fileBl.generatePDFFile(pdf!!, "application/pdf", companyId, headers, "Libro Diario" )
        return ResponseEntity.ok(
            ResponseDto(url, "Reporte obtenido con exito", true, "" ))
    }

}