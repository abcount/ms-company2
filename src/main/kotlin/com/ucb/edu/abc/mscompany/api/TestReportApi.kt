package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.PDFTurtleBl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pdf")
class TestReportApi @Autowired constructor(
    private val pdfTurtleBl: PDFTurtleBl
){
    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/test")
    suspend fun getPdfTest() {
        val footer = pdfTurtleBl.readHtmlToString("ReportPDF/libro-diario/footer.html")
        val header = pdfTurtleBl.readHtmlToString("ReportPDF/libro-diario/header.html")
        val body = pdfTurtleBl.readHtmlToString("ReportPDF/libro-diario/index.html")
        val model = pdfTurtleBl.readJson("ReportPDF/libro-diario/example-model.json")
        logger.info("Footer: $footer")
        logger.info("Header: $header")
        logger.info("Body: $body")
        logger.info("Model: $model")
        val pdf = pdfTurtleBl.getPDF(footer, header, body, model)
        logger.info("PDF: $pdf")
        pdfTurtleBl.byteArrayToFile(pdf!!, "test.pdf")
    }
}