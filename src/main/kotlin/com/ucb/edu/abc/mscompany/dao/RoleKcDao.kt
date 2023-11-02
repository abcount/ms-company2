package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.RoleEntity
import com.ucb.edu.abc.mscompany.entity.RolesKcEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component

@Mapper
@Component
interface RoleKcDao {

    @Options(useGeneratedKeys = true, keyProperty = "roleKcId")
    @Insert("""
        INSERT INTO role_kc
        (uuid_role, company_id ,complete_role, role_name)
        VALUES
        (#{uuidRole}, #{companyId}, #{completeRole}, #{roleName});
    """)
    fun createRoleInKc(roleKcEntity: RolesKcEntity)

    @Select("""
        SELECT * FROM role_kc 
        WHERE role_name = #{roleName}
        AND company_id = #{companyId};
    """)
    fun getRolesByRoleNameAndCompanyId(roleName:String, companyId:Int):List<RolesKcEntity>?

    @Select("""
        SELECT * FROM role_kc 
        WHERE uuid_role = #{roleUuid}
    """)
    fun getRoleByUuid(roleUuid:String): RolesKcEntity?

}