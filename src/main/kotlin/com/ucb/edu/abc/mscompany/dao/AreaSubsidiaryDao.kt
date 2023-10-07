package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.AreaSubsidiaryEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component

@Mapper
@Component
interface AreaSubsidiaryDao {

    @Options(useGeneratedKeys = true, keyProperty = "areaSubsidiaryId")
    @Insert("INSERT INTO area_subsidiary (subsidiary_id, area_id, dicc_category, date_created)" +
            " VALUES (#{subsidiaryId}, #{areaId}, #{diccCategory}, now())")
    fun create(areaSubsidiaryEntity: AreaSubsidiaryEntity)

    @Select("SELECT * FROM area_subsidiary WHERE area_id = #{areaId} AND subsidiary_id = #{subsidiaryId}")
    fun findByAreaAndSubsidiary(areaId: Int, subsidiaryId: Int): AreaSubsidiaryEntity?
    @Select("SELECT area_subsidiary_id FROM area_subsidiary WHERE subsidiary_id = #{subsidiaryId} and area_id = #{areaId}")
    fun findAreaSubsidiaryId(subsidiaryId: Int, areaId: Int): Int

}