package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.ExchangeRateEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component

@Mapper
@Component
interface ExchangeRateDao {

    @Select("SELECT * FROM exchange_rate WHERE exchange_rate_id = #{exchangeRateId}")
    fun getExchangeRateById(exchangeRateId: Int): ExchangeRateEntity

    @Select("SELECT * FROM exchange_rate WHERE date::date = current_date AND company_id = #{companyId}")
    fun getAllExchangeRateByCompanyIdAndDate(companyId: Int): List<ExchangeRateEntity>

    @Select("SELECT * FROM exchange_rate WHERE company_id = #{companyId}")
    fun getAllExchangeRateByCompanyId(companyId: Int): List<ExchangeRateEntity>

    /*TODO: revisar si obtener hora del servidor o de la base de datos*/
    @Insert("INSERT INTO exchange_rate (money_name, company_id, currency, abbreviation_name, date) " +
            "VALUES (#{moneyName}, #{companyId}, #{currency}, #{abbreviationName}, #{date})")
    fun createExchangeRate(exchangeRateEntity: ExchangeRateEntity)
}