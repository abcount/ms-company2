package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.TransactionTypeDao
import com.ucb.edu.abc.mscompany.dto.response.TransactionType
import com.ucb.edu.abc.mscompany.exception.EmptyResponseException
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TransactionTypeBl @Autowired constructor(
    private val transactionTypeDao: TransactionTypeDao
){

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getTransactionTypeByCompanyId(companyId: Int): List<TransactionType> {
        try {
            logger.info("Obteniendo tipos de transacciones por id de compañia")
            return transactionTypeDao.getTransactionTypeByCompanyId(companyId)
        } catch (e: Exception) {
            logger.error("Error al obtener tipos de transacciones por id de compañia", e)
            throw PostgresException("Error al obtener tipos de transacciones de la compañia: $companyId", e.message.toString())
        }
    }

}