package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.RoleEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component
import java.util.*

@Mapper
@Component
interface GroupDao {

    @Select("SELECT * FROM group_entity WHERE name = #{nameGroup}")
    fun getRoleByName(nameRole: String): Optional<RoleEntity>

    @Options(useGeneratedKeys = true, keyProperty = "roleId")
    @Insert("INSERT INTO role_entity (name, description, dicc_category, status, date_created, common_id) " +
            " VALUES (#{name}, #{description}, #{diccCategory}, #{status}, #{dateCreated}, #{commonId});")
    fun createRole(roleEntity: RoleEntity)
}