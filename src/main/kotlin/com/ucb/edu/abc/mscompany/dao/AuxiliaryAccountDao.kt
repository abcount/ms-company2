package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.AuxiliaryAccountEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component

@Mapper
@Component
interface AuxiliaryAccountDao {

    @Select("SELECT * FROM auxiliary_account WHERE company_id = #{companyId}")
    fun getAuxiliaryAccountByCompanyId(companyId: Int): List<AuxiliaryAccountEntity>

    @Options(useGeneratedKeys = true, keyProperty = "auxiliaryAccountId")
    @Insert("INSERT INTO auxiliary_account (code_account, name_description, company_id)" +
            " VALUES (#{codeAccount}, #{nameDescription}, #{companyId})")
    fun create(auxiliaryAccountEntity: AuxiliaryAccountEntity)

}