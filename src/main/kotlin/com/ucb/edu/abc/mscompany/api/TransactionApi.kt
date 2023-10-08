package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.TransactionBl
import com.ucb.edu.abc.mscompany.dto.request.TransactionDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import com.ucb.edu.abc.mscompany.dto.response.TransactionalVoucherDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/transactional/voucher")
class TransactionApi @Autowired constructor(
        private val transactionBl: TransactionBl,
) {
    @PostMapping("/{companyId}")
    fun saveTransaction(@PathVariable companyId: Int, @RequestBody transactionDto: TransactionDto): ResponseDto<String> {
         transactionBl.saveTransaction(companyId, transactionDto)
        return ResponseDto("", "Request exitoso", true, "", )
    }

    @GetMapping("/{companyId}")
    fun getDataTransaction(@PathVariable companyId: Int): ResponseEntity<ResponseDto<TransactionalVoucherDto>>{
        //TODO: enviar datos del usuario de la cabezera
        println(companyId)
        val transactionalVoucherDto = transactionBl.getDataForDoATransaction(companyId, "")
        return ResponseEntity.ok(
            ResponseDto(transactionalVoucherDto, "Datos obtenidos con exito", true, "" ))
    }
}