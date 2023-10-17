package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.ExchangeRateDao
import com.ucb.edu.abc.mscompany.entity.ExchangeRateEntity
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExchangeRateBl @Autowired constructor(
    private val exchangeRateDao: ExchangeRateDao
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getAllExchangeRateByCompanyId(companyId: Int): List<ExchangeRateEntity>{
        try {
            logger.info("Obteniendo todos los tipos de cambio por compañia")
            return exchangeRateDao.getAllExchangeRateByCompanyId(companyId)
        } catch (e: Exception){
            logger.error("Error al obtener todos los tipos de cambio por compañia", e)
            throw PostgresException("Error al obtener todos los tipos de cambio por compañia", e.message.toString())
        }
    }

    fun getAllExchangeRateByCompanyIdAndDate(companyId: Int): List<ExchangeRateEntity>{
        try {
            logger.info("Obteniendo todos los tipos de cambio por compañia y fecha")
            return exchangeRateDao.getAllExchangeRateByCompanyIdAndDate(companyId)
        } catch (e: Exception){
            logger.error("Error al obtener todos los tipos de cambio por compañia y fecha", e)
            throw PostgresException("Error al obtener todos los tipos de cambio por compañia y fecha", e.message.toString())
        }
    }
}