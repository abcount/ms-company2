package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.AccountEntity
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.Date

@Mapper
@Component
interface AccountDao {

    @Options(useGeneratedKeys = true, keyProperty = "accountId")
    @Insert("INSERT INTO account (company_id, account_account_id, code_account, name_account, clasificator, \"level\", report, status, money_rub)" +
            " VALUES (#{companyId}, #{accountAccountId}, #{codeAccount}, #{nameAccount}, #{clasificator}, #{level}, #{report}, #{status}, #{moneyRub})")
    fun create(accountEntity: AccountEntity)

    @Select("SELECT * FROM account WHERE company_id = #{companyId}")
    fun getAccountPlanByCompanyId(companyId: Int): MutableList<AccountEntity>

    @Delete("DELETE FROM account WHERE account_id = #{accountId}")
    fun delete(accountId: Int)

    @Select("SELECT * FROM account " +
            "WHERE company_id = #{companyId} " +
            "AND clasificator = false " +
            "AND money_rub = true ")
    fun getAccountsNonGrouperByCompanyId(companyId: Int): List<AccountEntity>

    @Select("SELECT EXISTS (" +
            "  SELECT 1 " +
            "  FROM transaction_account " +
            "  WHERE account_id = #{accountId} )")
    fun isEditable(accountId: Int): Boolean

    @Select("SELECT * FROM account WHERE account_id = #{accountId}")
    fun getAccountById(accountId: Int): AccountEntity

    @Select("SELECT account_id FROM account WHERE code_account = #{codeAccount} AND company_id = #{companyId}")
    fun getAccountIdBYCode(codeAccount: String, companyId: Int): Int


    // obtener balance por cuenta
    // si es pasivo o patrimonio es al revés credit suma y debit resta
    @Select("""
                SELECT 
            SUM(dc.amount_debit) - SUM(dc.amount_credit) AS balance
        FROM 
            transaction_account AS ta
        JOIN 
            transaction AS t ON ta.transaction_id = t.transaction_id
        JOIN 
            debit_credit AS dc ON ta.transaction_account_id = dc.transaction_account_id
        JOIN 
            exchange_rate AS er ON dc.exchange_rate_id = er.exchange_rate_id
        WHERE 
            ta.account_id = #{accountId} AND
            t.area_subsidiary_id = #{areaSubsidiaryId} AND
            er.abbreviation_name = #{exchangeMoneyIso}
            
            AND t.date <= #{date}; 
    """)
    fun getBalanceByAccount(accountId: Int, date: Date, areaSubsidiaryId: Int?, exchangeMoneyIso: String): BigDecimal

    @Select("""
                    SELECT 
                 SUM(dc.amount_credit) - SUM(dc.amount_debit)  AS balance
            FROM 
                transaction_account AS ta
            JOIN 
                transaction AS t ON ta.transaction_id = t.transaction_id
            JOIN 
                debit_credit AS dc ON ta.transaction_account_id = dc.transaction_account_id
            JOIN 
                exchange_rate AS er ON dc.exchange_rate_id = er.exchange_rate_id
            WHERE 
                ta.account_id = #{accountId} AND
                t.area_subsidiary_id = #{areaSubsidiaryId} AND
                er.abbreviation_name = #{exchangeMoneyIso}                 
                
                AND t.date <= #{date}; 
    """)
    fun getBalancePassive(accountId: Int, date: Date, areaSubsidiaryId: Int?, exchangeMoneyIso: String): BigDecimal


    @Select(
        """
            SELECT ac.code_account FROM account ac, company com
            WHERE com.company_id = #{companyId} 
            AND ac.company_id = com.company_id
            AND ac.level = 0
        """
    )
    fun getRootAccounts(companyId: Int): List<String>

    @Select(
        """
            SELECT ac.code_account FROM account ac, company com
            WHERE com.company_id = #{companyId} 
            AND ac.company_id = com.company_id
            AND ac.clasificator = false
        """
    )
    fun getMovementAccounts(companyId: Int): List<String>


    //para el estado de resultados
    @Select("""
                SELECT 
            SUM(dc.amount_debit) - SUM(dc.amount_credit) AS balance
        FROM 
            transaction_account AS ta
        JOIN 
            transaction AS t ON ta.transaction_id = t.transaction_id
        JOIN 
            debit_credit AS dc ON ta.transaction_account_id = dc.debit_credit_id
        JOIN 
            exchange_rate AS er ON dc.exchange_rate_id = er.exchange_rate_id
        WHERE 
            ta.account_id = #{accountId} AND
            t.area_subsidiary_id = #{areaSubsidiaryId} AND
            dc.exchange_rate_id = er.exchange_rate_id AND
            er.abbreviation_name = #{exchangeMoneyIso} AND
            
            t.date BETWEEN #{from} AND #{to}
    """)
    fun getStateByAccount(accountId: Int, from:Date, to:Date, areaSubsidiaryId: Int?, exchangeMoneyIso: String): BigDecimal


    @Select("""
                SELECT 
             SUM(dc.amount_credit) - SUM(dc.amount_debit)  AS balance
        FROM 
            transaction_account AS ta
        JOIN 
            transaction AS t ON ta.transaction_id = t.transaction_id
        JOIN 
            debit_credit AS dc ON ta.transaction_account_id = dc.debit_credit_id
        JOIN 
            exchange_rate AS er ON dc.exchange_rate_id = er.exchange_rate_id
        WHERE 
            ta.account_id = #{accountId} AND
            t.area_subsidiary_id = #{areaSubsidiaryId} AND
            er.abbreviation_name = #{exchangeMoneyIso} AND
            dc.exchange_rate_id = er.exchange_rate_id  AND
            
            t.date BETWEEN #{from} AND #{to}
    """)
    fun getStatePassive(accountId: Int, from: Date, to: Date, areaSubsidiaryId: Int?, exchangeMoneyIso: String): BigDecimal



}