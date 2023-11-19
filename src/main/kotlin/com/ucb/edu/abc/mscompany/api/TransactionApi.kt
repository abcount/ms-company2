package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.FileBl
import com.ucb.edu.abc.mscompany.bl.PDFTurtleBl
import com.ucb.edu.abc.mscompany.bl.TransactionBl
import com.ucb.edu.abc.mscompany.dto.request.TransactionDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import com.ucb.edu.abc.mscompany.dto.response.TransactionalVoucherDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/transactional/voucher")
class TransactionApi @Autowired constructor(
        private val transactionBl: TransactionBl,
        private val pdfTurtleBl: PDFTurtleBl,
        private val fileBl: FileBl
) {
    @PostMapping("/{companyId}")
    fun saveTransaction(@PathVariable companyId: Int, @RequestBody transactionDto: TransactionDto, @RequestHeader headers: Map<String, String>): ResponseDto<String> {
         transactionBl.saveTransaction(companyId, transactionDto, headers)
        return ResponseDto("", "Request exitoso", true, "", )
    }

    @GetMapping("/{companyId}")
    fun getDataTransaction(@PathVariable companyId: Int, @RequestHeader headers: Map<String, String>): ResponseEntity<ResponseDto<TransactionalVoucherDto>>{
        //TODO: enviar datos del usuario de la cabezera
        val transactionalVoucherDto = transactionBl.getDataForDoATransaction(companyId, headers)
        return ResponseEntity.ok(
            ResponseDto(transactionalVoucherDto, "Datos obtenidos con exito", true, "" ))
    }

    @GetMapping("/list/{companyId}")
    fun getListTransaction(@PathVariable companyId: Int, @RequestParam subsidiaryId: Int,
                            @RequestParam areaId: Int, @RequestParam transactionTypeId: Int,): ResponseEntity<ResponseDto<*>>{
        val listTransaction = transactionBl.getListTransaction(companyId, subsidiaryId, areaId, transactionTypeId)
        return ResponseEntity.ok(
            ResponseDto(listTransaction, "Datos obtenidos con exito", true, "" ))
    }

    @GetMapping("/currency/{companyId}")
    fun getTransactionWithCurrency(
            @PathVariable companyId: Int,
            @RequestParam transactionId: Int,
            @RequestParam currency: String): ResponseEntity<ResponseDto<*>>{
        val transaction = transactionBl.getTransactionByIdAndCurrency(companyId, transactionId, currency)
        return ResponseEntity.ok(
            ResponseDto(transaction, "Datos obtenidos con exito", true, "" ))
    }

    @GetMapping("/pdf/{companyId}")
    suspend fun getPDFTransaction(
            @PathVariable companyId: Int,
            @RequestParam transactionId: Int,
            @RequestParam currency: String,
            @RequestHeader headers: Map<String,String>): ResponseEntity<ResponseDto<*>>{
        val transaction = transactionBl.getPDFTransaction(companyId, transactionId, currency)
        val footer = pdfTurtleBl.readHtmlToString("ReportPDF/transaction/footer.html")
        val header = pdfTurtleBl.readHtmlToString("ReportPDF/transaction/header.html")
        val body = pdfTurtleBl.readHtmlToString("ReportPDF/transaction/index.html")
        val pdf = pdfTurtleBl.getPDF(footer, header, body, transaction)
        val url = fileBl.generatePDFFile(pdf!!, "application/pdf", companyId, headers, "Comprobante Diario" )
        return ResponseEntity.ok(
            ResponseDto(url, "Datos obtenidos con exito", true, "" ))
    }




}