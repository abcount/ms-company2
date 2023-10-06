package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.ExchangeEntity
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component

@Mapper
@Component
interface ExchangeDao {

    @Select("SELECT * " +
            "FROM exchange " +
            "WHERE unaccent(LOWER(money_name)) LIKE '%' || unaccent(LOWER(#{name})) || '%'" +
            "   OR unaccent(LOWER(money_iso)) LIKE '%' || unaccent(LOWER(#{name})) || '%'" +
            "   OR unaccent(LOWER(country)) LIKE '%' || unaccent(LOWER(#{name})) || '%'")
    fun getExchangeByNameOrIso(name: String): List<ExchangeEntity>

    @Select("SELECT * FROM exchange")
    fun getAllExchange(): List<ExchangeEntity>
}