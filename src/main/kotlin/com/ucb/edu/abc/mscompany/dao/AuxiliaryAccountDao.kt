package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.AuxiliaryAccountEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.springframework.stereotype.Component

@Mapper
@Component
interface AuxiliaryAccountDao {

    @Select("SELECT * FROM auxiliary_account WHERE company_id = #{companyId} order by auxiliary_account_id asc")
    fun getAuxiliaryAccountByCompanyId(companyId: Int): List<AuxiliaryAccountEntity>

    @Options(useGeneratedKeys = true, keyProperty = "auxiliaryAccountId")
    @Insert("INSERT INTO auxiliary_account (code_account, name_description, company_id)" +
            " VALUES (#{codeAccount}, #{nameDescription}, #{companyId})")
    fun create(auxiliaryAccountEntity: AuxiliaryAccountEntity)

    @Update("UPDATE auxiliary_account SET name_description = #{name}, code_account = #{code} WHERE auxiliary_account_id = #{auxiliaryId}")
    fun update(auxiliaryId: Int, name: String, code: String)

}