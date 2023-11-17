package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.ExchangeRateDao
import com.ucb.edu.abc.mscompany.dto.request.ExchangeDto
import com.ucb.edu.abc.mscompany.dto.request.UpdateExchangeRate
import com.ucb.edu.abc.mscompany.dto.response.CurrencyDateDto
import com.ucb.edu.abc.mscompany.dto.response.CurrencyVoucher
import com.ucb.edu.abc.mscompany.dto.response.ExchangeDateDto
import com.ucb.edu.abc.mscompany.dto.response.ListExchangeRateDateDto
import com.ucb.edu.abc.mscompany.entity.ExchangeRateEntity
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.LocalDate
import java.util.Date

@Service
class ExchangeRateBl @Autowired constructor(
    private val exchangeRateDao: ExchangeRateDao,
        private val exchangeMoneyBl: ExchangeMoneyBl
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

    fun createExchangeRateList(exchangeRateList: List<ExchangeDto>, companyId: Int, date: LocalDate){
        for (exchange in exchangeRateList){
            val exchangeRateEntity = ExchangeRateEntity(
                0,
                exchange.moneyName,
                companyId,
                exchange.currency,
                exchange.abbreviationName,
                Timestamp.valueOf(date.atStartOfDay())
            )
            createExchangeRate(exchangeRateEntity)
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
            return exchangeRateDao.getAllExchangeRateByCompanyIdToday(companyId)
        } catch (e: Exception){
            logger.error("Error al obtener todos los tipos de cambio por compañia y fecha", e)
            throw PostgresException("Error al obtener todos los tipos de cambio por compañia y fecha", e.message.toString())
        }
    }


    fun existRegister(companyId: Int): Boolean{
        try {
            logger.info("Verificando si existen registros de tipos de cambio")
            return exchangeRateDao.getAllExchangeRateByCompanyIdToday(companyId).isNotEmpty()
        } catch (e: Exception){
            logger.error("Error al verificar si existen registros de tipos de cambio", e)
            throw PostgresException("Error al verificar si existen registros de tipos de cambio", e.message.toString())
        }
    }

    fun existRegisterByDay(companyId: Int, date: LocalDate): Boolean{
        try {
            logger.info("Verificando si existen registros de tipos de cambio")
            return exchangeRateDao.getAllExchangeRateByCompanyIdAndDate(companyId, date).isNotEmpty()
        } catch (e: Exception){
            logger.error("Error al verificar si existen registros de tipos de cambio", e)
            throw PostgresException("Error al verificar si existen registros de tipos de cambio", e.message.toString())
        }
    }

    fun getExchangeRate(exchangeRateId: Int): CurrencyVoucher{
        try {
            logger.info("Obteniendo tipos de cambio por id")
            return exchangeRateDao.getExchangeRate(exchangeRateId)
        } catch (e: Exception){
            logger.error("Error al obtener tipos de cambio por id", e)
            throw PostgresException("Error al obtener tipos de cambio por id", e.message.toString())
        }
    }

    fun getListDates(companyId: Int): List<String>{
        try{
            logger.info("Obteniendo registro de fechas existentes")
            return exchangeRateDao.getListDates(companyId)
        } catch (e: Exception){
            logger.error("Error al obtener registro de fechas existentes", e)
            throw PostgresException("Error al obtener registro de fechas existentes", e.message.toString())
        }
    }

    fun getExchangeRateByDate(companyId: Int, date:String): List<ExchangeRateEntity>{
        try{
            logger.info("Obteniendo tipos de cambio por fecha")
            return exchangeRateDao.getExchangeRateByDate(companyId, date)
        } catch (e: Exception){
            logger.error("Error al obtener tipos de cambio por fecha", e)
            throw PostgresException("Error al obtener tipos de cambio por fecha", e.message.toString())
        }

    }

    fun getExchangeRateByDate(companyId: Int): List<ExchangeDateDto>{
        val listDate = getListDates(companyId)
        val listCurrencies: List<ExchangeDateDto> = listDate.map {
            val listExchangeRate = getExchangeRateByDate(companyId, it).map{ currency ->
                CurrencyDateDto(currency.exchangeRateId, currency.currency,currency.moneyName, currency.abbreviationName)
            }
            ExchangeDateDto(it, listExchangeRate)
        }
        return listCurrencies
    }


    fun getListExchangeRateGroupByDate(companyId: Int): ListExchangeRateDateDto{
        val exchangeMoney = exchangeMoneyBl.getAllCurrenciesByCompanyId(companyId)
        val list = getExchangeRateByDate(companyId)
        return ListExchangeRateDateDto(exchangeMoney, list)

    }

    fun updateExchangeRate(updateExchangeRate: UpdateExchangeRate){
        try{
            exchangeRateDao.updateExchangeRate(updateExchangeRate)
        }catch (e: Exception){
            logger.error("Error al actualizar tipos de cambio", e)
            throw PostgresException("Error al actualizar tipos de cambio", e.message.toString())
        }
    }

    fun updateListExchangeRate(listExchangeRate: List<UpdateExchangeRate>){
        for (exchangeRate in listExchangeRate){
            updateExchangeRate(exchangeRate)
        }
    }




}