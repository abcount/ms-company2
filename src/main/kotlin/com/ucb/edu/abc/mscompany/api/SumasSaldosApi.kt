package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.FileBl
import com.ucb.edu.abc.mscompany.bl.PDFTurtleBl
import com.ucb.edu.abc.mscompany.bl.ReportBl
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
    private val sumasSaldosBl: SumasSaldosBl,
        private val pdfTurtleBl: PDFTurtleBl,
        private val fileBl: FileBl,
        private val reportBl: ReportBl
){
    @PostMapping("/{companyId}")
    fun getSumasSaldos(
        @PathVariable companyId: Int,
        @RequestBody sumasSaldosRequestDto: SumasSaldosRequestDto,
        @RequestHeader headers: Map<String,String>): ResponseEntity<ResponseDto<SumasSaldosResponseDto>> {
        val sumasSaldosResponseDto = sumasSaldosBl.getSumasSaldos(companyId, sumasSaldosRequestDto)
        val reportEntity = reportBl.factoryReportEntity(headers, companyId, "", "EXCEL", "Sumas y Saldos")
        reportBl.createReport(reportEntity)
        return ResponseEntity.ok(ResponseDto(sumasSaldosResponseDto, "Sumas y saldos obtenidos con exito", true, "" ))
    }

    @PostMapping("/pdf/{companyId}")
    suspend fun getSumasSaldosPDF(
            @PathVariable companyId: Int,
            @RequestBody sumasSaldosRequestDto: SumasSaldosRequestDto,
            @RequestHeader headers: Map<String, String>
    ): ResponseEntity<ResponseDto<String>>{
        val sumasSaldosResponseDto = sumasSaldosBl.getSumasSaldos(companyId, sumasSaldosRequestDto)
        val footer = pdfTurtleBl.readHtmlToString("ReportPDF/SUMAS-SALDOS/footer.html")
        val header = pdfTurtleBl.readHtmlToString("ReportPDF/SUMAS-SALDOS/header.html")
        val body = pdfTurtleBl.readHtmlToString("ReportPDF/SUMAS-SALDOS/index.html")
        val pdf = pdfTurtleBl.getPDF(footer, header, body, sumasSaldosResponseDto)
        val url = fileBl.generatePDFFile(pdf!!, "application/pdf", companyId, headers, "Sumas y Saldos")
        return ResponseEntity.ok(
                ResponseDto(url, "Reporte obtenido con exito", true, "" ))
    }
}