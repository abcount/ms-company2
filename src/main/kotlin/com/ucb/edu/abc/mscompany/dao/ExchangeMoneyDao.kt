package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.ExchangeMoneyEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component

@Mapper
@Component
interface ExchangeMoneyDao {
    @Options(useGeneratedKeys = true, keyProperty = "exchangeMoneyId")
    @Insert("INSERT INTO exchange_money (company_id, money_name, abbreviation_name, is_principal)" +
            "VALUES (#{companyId}, #{moneyName}, #{abbreviationName}, #{isPrincipal})")
    fun create(exchangeMoneyEntity: ExchangeMoneyEntity)

    @Select("SELECT * FROM exchange_money WHERE company_id = #{companyId}")
    fun getAllCurrenciesByCompanyId(companyId: Int): List<ExchangeMoneyEntity>
}