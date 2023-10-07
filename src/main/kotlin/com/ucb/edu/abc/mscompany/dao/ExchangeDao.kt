package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.ExchangeEntity
import org.apache.ibatis.annotations.Insert
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

    @Insert("INSERT INTO exchange (money_name, money_iso, country) " +
            "VALUES (#{moneyName}, #{moneyIso}, #{country})")
    fun create(exchangeEntity: ExchangeEntity): Int

    @Select("SELECT * FROM exchange WHERE exchange_id = #{exchangeId}")
    fun getExchangeById(exchangeId: Int): ExchangeEntity

    @Select("SELECT * FROM exchange WHERE money_iso = 'BOL' and money_name = 'BOLIVIANO' limit 1")
    fun getBoliviano(): ExchangeEntity

    @Select("""
            <script>
            SELECT * FROM exchange WHERE exchange_id IN 
            <foreach collection="exchangeIds" item="id" open="(" separator="," close=")">
                #{id}
            </foreach>
            </script>
        """)
    fun getExchangesByArrayId(exchangeIds: List<Int>): List<ExchangeEntity>

}