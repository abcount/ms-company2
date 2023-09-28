package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.AccountEntity
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.springframework.stereotype.Component

@Mapper
@Component
interface AccountDao {

    /*
    account_id serial  NOT NULL,
    company_id int  NOT NULL,
    account_account_id int  NULL,
    code_account varchar(15)  NOT NULL,
    name_account varchar(150)  NOT NULL,
    clasificator boolean  NOT NULL,
    "level" int  NOT NULL,
    report boolean  NOT NULL,
    status boolean  NOT NULL,
    money_rub boolean  NOT NULL
     */
    @Options(useGeneratedKeys = true, keyProperty = "accountId")
    @Insert("INSERT INTO account (company_id, account_account_id, code_account, name_account, clasificator, \"level\", report, status, money_rub)" +
            " VALUES (#{companyId}, #{accountAccountId}, #{codeAccount}, #{nameAccount}, #{clasificator}, #{level}, #{report}, #{status}, #{moneyRub})")
    fun create(accountEntity: AccountEntity)

    @Delete("DELETE FROM account WHERE account_id = #{accountId}")
    fun delete(accountId: Int)

}