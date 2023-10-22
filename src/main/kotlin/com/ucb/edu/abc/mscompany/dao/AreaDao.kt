package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.AreaEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.springframework.stereotype.Component

@Mapper
@Component
interface AreaDao {

    @Options(useGeneratedKeys = true, keyProperty = "areaId")
    @Insert("INSERT INTO area (company_id, area_name, date_created, status, common_id) VALUES (#{companyId}, #{areaName}, #{dateCreated}, #{status}, #{commonId})")
    fun create(area: AreaEntity)

    @Select("SELECT * FROM area WHERE area_id = #{areaId}")
    fun getAreaById(areaId: Int): AreaEntity

    @Select("SELECT * FROM area WHERE company_id = #{companyId} and status = 'true'")
    fun getAreasByCompanyId(companyId: Int): List<AreaEntity>

    @Select("""
                SELECT DISTINCT 
                    a.*
                FROM
                    abc_permission ap
                JOIN
                    area_subsidiary asub ON ap.area_subsidiary_id = asub.area_subsidiary_id
                JOIN
                    area a ON asub.area_id = a.area_id
                WHERE
                    ap.user_id = #{userId} AND
                    a.company_id = #{companyId} AND
                    a.status = true
    """)
    fun getAreasByUserIdAndCompanyId(userId: Int, companyId: Int): List<AreaEntity>


    @Update("UPDATE area SET status = 'false' WHERE area_id = #{areaId}")
    fun updateStatus(areaId: Int)


}