package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.AreaEntity
import com.ucb.edu.abc.mscompany.entity.RoleEntity
import com.ucb.edu.abc.mscompany.entity.SubsidiaryEntity
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Component
import java.util.Optional

@Mapper
@Component
interface RoleDao {
    @Select("SELECT * FROM role_entity WHERE name = #{nameRole}")
    fun getRoleByName(nameRole: String): RoleEntity?

    @Select("SELECT * FROM role_entity WHERE role_id = #{roleId}")
    fun getRoleById(roleId: Int): RoleEntity?

    @Options(useGeneratedKeys = true, keyProperty = "roleId")
    @Insert("INSERT INTO role_entity (name, description, dicc_category, status, date_created, common_id) " +
            " VALUES (#{name}, #{description}, #{diccCategory}, #{status}, #{dateCreated}, #{commonId});")
    fun createRole(roleEntity: RoleEntity)

    @Select("SELECT * FROM role_entity")
    fun getAllRoles(): List<RoleEntity>

    @Select("""
        SELECT 
        ro.role_id, ro.name, ro.description, ro.dicc_category, ro.status,
        ro.date_created, ro.common_id 
        FROM 
        role_entity ro,
        group_role grro,
        group_entity gr,
        group_access_entity grae,
        abc_permission perm,
        abc_user usr
        WHERE 
            ro.role_id = grro.role_id 
        AND grro.group_id = gr.group_id
        AND grae.group_id = gr.group_id
        AND grae.abc_permission_id = perm.permission_id
        AND perm.user_id = usr.user_id
        
        AND usr.status = true
        AND usr.dicc_category = #{dicCategoryUser}
        AND perm.status = true
        AND gr.status = true
        AND grro.status = #{statusGroupRole}
        AND usr.user_id = #{userId}
    """)
    fun getRolesByUserAndUserCategory(dicCategoryUser: String, statusGroupRole: Boolean, userId: Int): List<RoleEntity>?

    @Select("""
        SELECT 
        ro.role_id, ro.name, ro.description, ro.dicc_category, ro.status,
        ro.date_created, ro.common_id 
        FROM 
        role_entity ro,
        group_role grro,
        group_entity gr,
        group_access_entity grae,
        abc_permission perm,
        abc_user usr
        WHERE 
            ro.role_id = grro.role_id 
        AND grro.group_id = gr.group_id
        AND grae.group_id = gr.group_id
        AND grae.abc_permission_id = perm.permission_id
        AND perm.user_id = usr.user_id
      
        AND grro.status = #{statusGroupRole}
        AND usr.user_id = #{userId}
    """)
    fun getRolesByUserAndUserCategoryNOFilters( statusGroupRole: Boolean, userId: Int): List<RoleEntity>?


    @Select("""
        SELECT 
        ro.role_id, ro.name, ro.description, ro.dicc_category, ro.status,
        ro.date_created, ro.common_id 
        FROM 
        role_entity ro,
        group_role grro,
        group_entity gr,
        group_access_entity grae,
        abc_permission perm,
        abc_user usr
        WHERE 
            ro.role_id = grro.role_id 
        AND grro.group_id = gr.group_id
        AND grae.group_id = gr.group_id
        AND grae.abc_permission_id = perm.permission_id
        AND perm.user_id = usr.user_id
       
        AND usr.user_id = #{userId}
    """)
    fun getRolesByUser(  userId: Int): List<RoleEntity>?



}