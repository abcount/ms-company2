package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.ExchangeRateEntity
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component

@Mapper
@Component
interface ExchangeRate {

    @Select("SELECT * FROM exchange_rate WHERE exchange_rate_id = #{exchangeRateId}")
    fun getExchangeRateById(exchangeRateId: Int): ExchangeRateEntity
}