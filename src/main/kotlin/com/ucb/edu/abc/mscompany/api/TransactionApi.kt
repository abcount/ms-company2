package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.TransactionBl
import com.ucb.edu.abc.mscompany.dto.request.TransactionDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import org.springframework.beans.factory.annotation.Autowired
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
}