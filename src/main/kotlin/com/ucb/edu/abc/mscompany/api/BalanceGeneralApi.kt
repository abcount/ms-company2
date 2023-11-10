package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.BalanceGeneralBl
import com.ucb.edu.abc.mscompany.bl.FileBl
import com.ucb.edu.abc.mscompany.bl.PDFTurtleBl
import com.ucb.edu.abc.mscompany.bl.ReportBl
import com.ucb.edu.abc.mscompany.dto.request.BalanceGeneralRequestDto
import com.ucb.edu.abc.mscompany.dto.response.BalanceGeneralResponseDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/general/balance")
class BalanceGeneralApi @Autowired constructor(
        private val balanceGeneralBl: BalanceGeneralBl,
        private val pdfTurtleBl: PDFTurtleBl,
        private val fileBl: FileBl,
        private val reportBl: ReportBl
){

   @PostMapping("/{companyId}")
    fun getBalanceGeneral(
           @PathVariable companyId: Int,
           @RequestBody balanceGeneralRequestDto: BalanceGeneralRequestDto,
           @RequestHeader headers: Map<String,String>): ResponseEntity<ResponseDto<BalanceGeneralResponseDto>> {
        val balanceGeneralResponseDto = balanceGeneralBl.getBalanceGeneral(companyId, balanceGeneralRequestDto)
        val reportEntity =reportBl.factoryReportEntity(headers, companyId, "", "EXCEL", "Balance General")
        reportBl.createReport(reportEntity)
        return ResponseEntity.ok(
                ResponseDto(balanceGeneralResponseDto, "Datos obtenidos con exito", true, "" ))
    }

    @PostMapping("/pdf/{companyId}")
    suspend fun generateBalanceGeneralPDF(
            @PathVariable companyId: Int,
            @RequestBody balanceGeneralRequestDto: BalanceGeneralRequestDto,
            @RequestHeader headers: Map<String, String>
    ): ResponseEntity<ResponseDto<String>> {
        val balanceGeneralResponseDto = balanceGeneralBl.getBalanceGeneralPDF(companyId, balanceGeneralRequestDto)
        val footer = pdfTurtleBl.readHtmlToString("ReportPDF/balance-sheet-pdf/footer.html")
        val header = pdfTurtleBl.readHtmlToString("ReportPDF/balance-sheet-pdf/header.html")
        val body = pdfTurtleBl.readHtmlToString("ReportPDF/balance-sheet-pdf/index.html")
        val pdf = pdfTurtleBl.getPDF(footer, header, body, balanceGeneralResponseDto)
        val url = fileBl.generatePDFFile(pdf!!, "application/pdf", companyId, headers, "Balance General")
        return ResponseEntity.ok(
                ResponseDto(url, "Reporte obtenido con exito", true, "" ))
    }



}
