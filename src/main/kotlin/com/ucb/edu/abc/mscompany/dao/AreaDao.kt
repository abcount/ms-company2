package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.AreaEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component

@Mapper
@Component
interface AreaDao {

    @Options(useGeneratedKeys = true, keyProperty = "areaId")
    @Insert("INSERT INTO area (company_id, area_name, date_created, status, common_id) VALUES (#{companyId}, #{areaName}, #{dateCreated}, #{status}, #{commonId})")
    fun create(area: AreaEntity)

    @Select("SELECT * FROM area WHERE area_id = #{areaId}")
    fun getAreaById(areaId: Int): AreaEntity

    @Select("SELECT * FROM area WHERE company_id = #{companyId}")
    fun getAreasByCompanyId(companyId: Int): List<AreaEntity>


}