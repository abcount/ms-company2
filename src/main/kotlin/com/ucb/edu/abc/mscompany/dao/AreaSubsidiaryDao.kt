package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.AreaSubsidiaryEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.springframework.stereotype.Component

@Mapper
@Component
interface AreaSubsidiaryDao {

    @Options(useGeneratedKeys = true, keyProperty = "areaSubsidiaryId")
    @Insert("INSERT INTO area_subsidiary (subsidiary_id, area_id, dicc_category, date_created)" +
            " VALUES (#{subsidiaryId}, #{areaId}, #{diccCategory}, now())")
    fun create(areaSubsidiaryEntity: AreaSubsidiaryEntity)


}