package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.EstadoResultadosBl
import com.ucb.edu.abc.mscompany.bl.FileBl
import com.ucb.edu.abc.mscompany.bl.PDFTurtleBl
import com.ucb.edu.abc.mscompany.bl.ReportBl
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
        private val estadoResultadosBl: EstadoResultadosBl,
        private val reportBl: ReportBl,
        private val fileBl: FileBl,
        private val pdfTurtleBl: PDFTurtleBl
) {
    @PostMapping("/{companyId}")
    fun getEstadoResultados(
            @PathVariable companyId: Int,
            @RequestBody estadoResultadosRequestDto: EstadoResultadosRequestDto,
            @RequestHeader headers: Map<String, String>
    ): ResponseEntity<ResponseDto<EstadoResultadosResponseDto>> {

        val resultStateResponseDto = estadoResultadosBl.getEstadoResultados(companyId, estadoResultadosRequestDto)
        val reportEntity = reportBl.factoryReportEntity(headers, companyId, "", "EXCEL", "Estado de Resultados")
        reportBl.createReport(reportEntity)
        return ResponseEntity.ok(
                ResponseDto(resultStateResponseDto, "Datos obtenidos con exito", true, "" ))
    }

    @PostMapping("/pdf/{companyId}")
    suspend fun getEstadosResultadosPDF(
            @PathVariable companyId: Int,
            @RequestBody estadoResultadosRequestDto: EstadoResultadosRequestDto,
            @RequestHeader headers: Map<String, String>
    ): ResponseEntity<ResponseDto<String>> {
        val result = estadoResultadosBl.getEstadoResultadosPDF(companyId, estadoResultadosRequestDto, headers)
        val footer = pdfTurtleBl.readHtmlToString("ReportPDF/estado-resultados/footer.html")
        val header = pdfTurtleBl.readHtmlToString("ReportPDF/estado-resultados/header.html")
        val body = pdfTurtleBl.readHtmlToString("ReportPDF/estado-resultados/index.html")

        val pdf = pdfTurtleBl.getPDF(footer, header, body, result!!)
        val url = fileBl.generatePDFFile(pdf!!, "application/pdf", companyId, headers, "Estado de Resultados")
        return ResponseEntity.ok(
                ResponseDto(url, "Reporte obtenido con exito", true, "" ))
    }


}