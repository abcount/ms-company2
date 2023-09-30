package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.SubsidiaryEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.springframework.stereotype.Component

@Mapper
@Component
interface SubsidiaryDao {

    @Select("SELECT * FROM subsidiary WHERE subsidiary_id = #{subsidiaryId}")
    fun getSubsidiaryById(subsidiaryId: Int): SubsidiaryEntity

    @Options(useGeneratedKeys = true, keyProperty = "subsidiaryId")
    @Insert("INSERT INTO subsidiary (company_id, subsidiary_name, address)" +
            " VALUES (#{companyId}, #{subsidiaryName}, #{address})")
    fun create(subsidiary: SubsidiaryEntity)

    @Update("UPDATE subsidiary SET company_id = #{companyId}, subsidiary_name = #{subsidiaryName}, " +
            "address = #{address} WHERE subsidiary_id = #{subsidiaryId}")
    fun update(subsidiary: SubsidiaryEntity): Int

    @Select("SELECT * FROM subsidiary WHERE company_id = #{companyId} ")
    fun getSubsidiariesByCompanyId(companyId: Int): List<SubsidiaryEntity>


}