package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.ExchangeMoneyDao
import com.ucb.edu.abc.mscompany.entity.ExchangeMoneyEntity
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.SQLException
import java.text.ParseException

@Service
class ExchangeMoneyBl @Autowired constructor(
        private val exchangeMoneyDao: ExchangeMoneyDao
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

    fun factoryExchangeMoney(companyId: Int, name: String, abbreviation: String, isPrincipal:Boolean): ExchangeMoneyEntity{
        var exchangeMoneyEntity = ExchangeMoneyEntity()
        exchangeMoneyEntity.companyId = companyId
        exchangeMoneyEntity.moneyName = name
        exchangeMoneyEntity.abbreviationName = abbreviation
        exchangeMoneyEntity.isPrincipal = isPrincipal
        return exchangeMoneyEntity
    }


}