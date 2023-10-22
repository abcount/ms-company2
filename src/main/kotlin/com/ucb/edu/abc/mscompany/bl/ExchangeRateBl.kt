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

    fun createExchangeRate(exchangeRateEntity: ExchangeRateEntity){
        try {
            logger.info("Creando tipos de cambio")
            exchangeRateDao.createExchangeRate(exchangeRateEntity)
        } catch (e: Exception){
            logger.error("Error al crear tipos de cambio", e)
            throw PostgresException("Error al crear tipos de cambio", e.message.toString())
        }
    }

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


    fun existRegister(companyId: Int): Boolean{
        try {
            logger.info("Verificando si existen registros de tipos de cambio")
            return exchangeRateDao.getAllExchangeRateByCompanyIdAndDate(companyId).isNotEmpty()
        } catch (e: Exception){
            logger.error("Error al verificar si existen registros de tipos de cambio", e)
            throw PostgresException("Error al verificar si existen registros de tipos de cambio", e.message.toString())
        }
    }
}