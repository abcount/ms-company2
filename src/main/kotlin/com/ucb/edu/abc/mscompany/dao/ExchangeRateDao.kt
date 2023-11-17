package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.dto.request.UpdateExchangeRate
import com.ucb.edu.abc.mscompany.dto.response.CurrencyVoucher
import com.ucb.edu.abc.mscompany.entity.ExchangeMoneyEntity
import com.ucb.edu.abc.mscompany.entity.ExchangeRateEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.LocalDate
import java.util.Date

@Mapper
@Component
interface ExchangeRateDao {

    @Select("SELECT * FROM exchange_rate WHERE exchange_rate_id = #{exchangeRateId}")
    fun getExchangeRateById(exchangeRateId: Int): ExchangeRateEntity

    @Select("SELECT * FROM exchange_rate WHERE date::date = #{date} AND company_id = #{companyId}")
    fun getAllExchangeRateByCompanyIdAndDate(companyId: Int, date: LocalDate): List<ExchangeRateEntity>

    @Select("SELECT * FROM exchange_rate WHERE date::date = current_date AND company_id = #{companyId}")
    fun getAllExchangeRateByCompanyIdToday(companyId: Int): List<ExchangeRateEntity>

    @Select("SELECT * FROM exchange_rate WHERE company_id = #{companyId}")
    fun getAllExchangeRateByCompanyId(companyId: Int): List<ExchangeRateEntity>

    //TODO: revisar si obtener hora del servidor o de la base de datos/
    @Insert("INSERT INTO exchange_rate (money_name, company_id, currency, abbreviation_name, date) " +
            "VALUES (#{moneyName}, #{companyId}, #{currency}, #{abbreviationName}, #{date})")
    fun createExchangeRate(exchangeRateEntity: ExchangeRateEntity)

    @Select("SELECT e.exchange_rate_id, e.money_name, e.abbreviation_name, e.currency " +
            "FROM exchange_rate e " +
            "WHERE exchange_rate_id = #{exchangeRateId}")
    fun getExchangeRate(exchangeRateId: Int): CurrencyVoucher

    @Select("SELECT DISTINCT TO_CHAR(date, 'YYYY-MM-DD') " +
            "FROM exchange_rate " +
            "WHERE company_id = #{companyId}")
    fun getListDates(companyId: Int): List<String>


    @Select("SELECT * FROM exchange_rate WHERE company_id = #{companyId} AND date::date = TO_DATE(#{date}, 'YYYY-MM-DD')")
    fun getExchangeRateByDate(companyId: Int, date: String): List<ExchangeRateEntity>

    @Update("UPDATE exchange_rate SET currency = #{currency} WHERE exchange_rate_id = #{exchangeRateId}")
    fun updateExchangeRate(updateExchangeRate: UpdateExchangeRate)


    @Select("SELECT * FROM exchange_rate WHERE company_id = #{companyId} AND abbreviation_name = #{abbreviationName} AND date =#{date}")
    fun getExchangeByCompanyIdAndAbbreviationName(companyId: Int, abbreviationName: String, date: LocalDateTime): ExchangeRateEntity

    @Select("SELECT * FROM exchange_rate WHERE company_id = #{companyId} AND date =#{date}")
    fun getExchangeList(companyId: Int, date: LocalDateTime): List<ExchangeRateEntity>


}