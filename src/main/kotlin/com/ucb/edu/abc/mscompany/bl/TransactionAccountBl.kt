package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.TransactionAccountDao
import com.ucb.edu.abc.mscompany.dto.response.TransactionListDto
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TransactionAccountBl @Autowired constructor(
    private val transactionAccountDao: TransactionAccountDao
){
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getAllTransactionByTransactionId(transactionId: Int, exchangeRateId: Int): List<TransactionListDto>{
        try{
            logger.info("Obteniendo todas las transacciones por id de transaccion")
            return transactionAccountDao.getTransactionAccount(transactionId, exchangeRateId)
        } catch (e: Exception){
            logger.error("Error al obtener todas las transacciones por id de transaccion", e)
            throw PostgresException("Error al obtener todas las transacciones por id de transaccion", e.message.toString())
        }
    }

    fun getAllTransactionByTransactionIdAndCurrencyIso(transactionId: Int, currencyIso: String): List<TransactionListDto>{
        try{
            logger.info("Obteniendo todas las transacciones por id de transaccion y iso de moneda")
            return transactionAccountDao.getTransactionAccountByCurrencyIso(transactionId, currencyIso)
        } catch (e: Exception){
            logger.error("Error al obtener todas las transacciones por id de transaccion y iso de moneda", e)
            throw PostgresException("Error al obtener todas las transacciones por id de transaccion y iso de moneda", e.message.toString())
        }
    }

}
