package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.AreaEntity
import com.ucb.edu.abc.mscompany.entity.RoleEntity
import com.ucb.edu.abc.mscompany.entity.SubsidiaryEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component
import java.util.Optional

@Mapper
@Component
interface RoleDao {
    @Select("SELECT * FROM role_entity WHERE name = #{nameRole}")
    fun getRoleByName(nameRole: String): Optional<RoleEntity>

    @Options(useGeneratedKeys = true, keyProperty = "roleId")
    @Insert("INSERT INTO role_entity (name, description, dicc_category, status, date_created, common_id) " +
            " VALUES (#{name}, #{description}, #{diccCategory}, #{status}, #{dateCreated}, #{commonId});")
    fun createRole(roleEntity: RoleEntity)

    @Select("SELECT * FROM role_entity")
    fun getAllRoles(): List<RoleEntity>

}