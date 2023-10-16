package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.AccountEntity
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component

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


}