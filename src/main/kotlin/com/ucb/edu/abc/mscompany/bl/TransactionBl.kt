package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.TransactionDao
import com.ucb.edu.abc.mscompany.dto.request.TransactionDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TransactionBl @Autowired constructor(
       transactionDao: TransactionDao
){
    private val logger: Logger = LoggerFactory.getLogger(CompanyBl::class.java)

    fun saveTransaction(companyId: Int, transactionDto: TransactionDto){
        logger.info("Guardando transaccion")
        if (transactionDto.totalCredit != transactionDto.totalDebit){
            throw Exception("El total de creditos y debitos no coinciden")
        }


    }

}