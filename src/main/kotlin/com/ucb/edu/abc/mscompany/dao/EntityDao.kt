package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.EntityEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.springframework.stereotype.Component

@Mapper
@Component
interface EntityDao {

    @Select("SELECT * FROM entity WHERE entity_id = #{entityId}")
    fun getEntityById(entityId: Int): EntityEntity

    @Insert("INSERT INTO entity (company_id, entity_name, nit, social_reason, externo)" +
            " VALUES (#{companyId}, #{entityName}, #{nit}, #{socialReason}, #{foreign})")
    fun create(entityEntity: EntityEntity)

    @Select("SELECT * FROM entity WHERE company_id = #{companyId} order by entity_id asc")
    fun getEntityByCompanyId(companyId: Int): List<EntityEntity>

    @Update("UPDATE entity SET entity_name = #{entityName}, nit = #{nit}, social_reason = #{socialReason}, externo = #{foreign} WHERE entity_id = #{entityId}")
    fun update(entityEntity: EntityEntity)

}