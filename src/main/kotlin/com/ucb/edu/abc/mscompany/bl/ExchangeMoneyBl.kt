package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.ExchangeMoneyDao
import com.ucb.edu.abc.mscompany.entity.ExchangeEntity
import com.ucb.edu.abc.mscompany.entity.ExchangeMoneyEntity
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.SQLException
import java.text.ParseException

@Service
class ExchangeMoneyBl @Autowired constructor(
        private val exchangeMoneyDao: ExchangeMoneyDao,
        private val exchangeBl: ExchangeBl
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun create(exchangeMoneyEntity: ExchangeMoneyEntity): Int{
        try {
            logger.info("Creando tipo de cambio")
            exchangeMoneyDao.create(exchangeMoneyEntity)
            return exchangeMoneyEntity.exchangeMoneyId
        } catch (e: Exception){
            throw PostgresException("Ocurrio un error al crear el tipo de cambio: ${exchangeMoneyEntity.toString()}", e.message.toString())
        }
    }

    fun getAllCurrenciesByCompanyId(companyId: Int): List<ExchangeMoneyEntity>{
        try {
            logger.info("Obteniendo todos los tipos de cambio por compañia")
            return exchangeMoneyDao.getAllCurrenciesByCompanyId(companyId)
        } catch (e: Exception){
            //TODO: Lanzar un error 404
            throw PostgresException("Ocurrio un error al obtener los tipos de cambio por compañia: ${companyId}", e.message.toString())
        }
    }

    fun createByListExchangeId(listExchangeId: List<Int>, companyId: Int): List<ExchangeMoneyEntity>{
        for(id in listExchangeId){
            val exchangeBl = exchangeBl.getById(id)
            val exchangeMoneyEntity = factoryExchangeMoney(exchangeBl, companyId, false)
            create(exchangeMoneyEntity)
        }
        return getAllCurrenciesByCompanyId(companyId)
    }

    fun factoryExchangeMoney(exchangeEntity: ExchangeEntity, companyId: Int, isPrincipal: Boolean): ExchangeMoneyEntity{
        var exchangeMoneyEntity = ExchangeMoneyEntity()
        exchangeMoneyEntity.companyId = companyId
        exchangeMoneyEntity.moneyName = exchangeEntity.moneyName + " - " + exchangeEntity.country
        exchangeMoneyEntity.abbreviationName = exchangeEntity.moneyIso
        exchangeMoneyEntity.isPrincipal = isPrincipal
        return exchangeMoneyEntity
    }

    fun getExchangeMoneyByCompanyIdAndISO(companyId: Int, iso: String): ExchangeMoneyEntity{
        try {
            logger.info("Obteniendo tipo de cambio por compañia e ISO")
            return exchangeMoneyDao.getCurrencyByCompanyIdAndAbbreviationName(companyId, iso)
        } catch (e: Exception) {
            logger.error("Ocurrio un error al obtener el tipo de cambio por compañia e ISO: ${companyId} - ${iso}")
            throw PostgresException("Ocurrio un error al obtener el tipo de cambio por compañia e ISO: ${companyId} - ${iso}", e.message.toString())
        }
    }
}