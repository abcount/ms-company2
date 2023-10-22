package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.GroupEntity
import com.ucb.edu.abc.mscompany.entity.PermissionEntity
import com.ucb.edu.abc.mscompany.entity.pojos.SubsidiaryAndAreaPojo
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.springframework.stereotype.Component
import java.util.Optional
import javax.websocket.server.ServerEndpoint

@Mapper
@Component
interface PermissionDao {

    @Insert("INSERT INTO abc_permission (area_subsidiary_id, user_id , status, dic_category ) " +
            " VALUES ( #{areaSubId}, #{userId}, #{status}, #{dicCategory} ) RETURNING permission_id;")
    @Options(useGeneratedKeys = true, keyProperty = "map.permission_id")
    fun createPermission(areaSubId: Int, status:Boolean, userId: Int, dicCategory:String,  @Param("map") map: HashMap<String, Any>)

    @Insert("INSERT INTO group_access_entity (group_id, date_created , abc_permission_id ) " +
            " VALUES ( #{groupId}, now() , #{permissionId} ) RETURNING group_access_entity_id;")
    @Options(useGeneratedKeys = true, keyProperty = "map.group_access_entity_id")
    fun createGroupPermission(groupId: Int, permissionId: Int,  @Param("map") map: HashMap<String, Int>)

    @Delete("""
        DELETE FROM group_access_entity
        WHERE abc_permission_id = #{permissionId}
        AND group_id = #{groupId}
    """)
    fun deleteGroupPermissionsByPermission(permissionId: Int, groupId: Int)

    @Delete("""
        DELETE FROM abc_permission
        WHERE permission_id = #{permissionId}
    """)
    fun deletePermissionById(permissionId: Int)

    @Select("""
        SELECT * FROM abc_permission
        WHERE user_id = #{userId}
        AND status = #{status}
    """)
    fun getPermissionsByUserAndStatus(userId: Int, status: Boolean): List<PermissionEntity>

    @Select("""
        SELECT * FROM abc_permission
        WHERE user_id = #{userId}
    """)
    fun findAllByUserId(userId: Int)
    @Update("""
        UPDATE abc_permission 
        SET status = #{status}
        WHERE permission_id = #{permissionId}
    """)
    fun updateStatusByPermissionId(status: Boolean, permissionId: Int)

    @Select("""
        SELECT 
        ars.area_subsidiary_id, s.subsidiary_id, a.area_id, ars.dicc_category, ars.date_created,
        a.area_name, s.subsidiary_name , perm.permission_id
        FROM 
        area_subsidiary ars
        JOIN subsidiary s
        ON ars.subsidiary_id = s.subsidiary_id
        JOIN area a
        ON ars.area_id = a.area_id
        AND a.status = true
        JOIN company c
        ON c.company_id =  a.company_id
        AND c.company_id = s.company_id
        JOIN abc_permission perm
        ON perm.area_subsidiary_id = ars.area_subsidiary_id
        JOIN abc_user usr
        ON usr.user_id = perm.user_id
        AND usr.user_id = #{userId}
    """)
    fun findByUserId(userId: Int): List<SubsidiaryAndAreaPojo>

    @Select("""
        SELECT per.*
        FROM abc_permission per
        JOIN area_subsidiary ars
        ON ars.area_subsidiary_id = per.area_subsidiary_id
        AND per.status = true
        AND ars.area_subsidiary_id = #{areaSubId}
        JOIN abc_user usr
        ON usr.user_id = per.user_id
        AND usr.user_id = #{userId}
        AND usr.status = true
        AND usr.dicc_category = 'ACTIVE'
        JOIN group_access_entity gper
        ON gper.abc_permission_id = per.permission_id
        JOIN group_entity gr
        ON gr.group_id = gper.group_id
        AND gr.status = true
        JOIN group_role  grole
        ON grole.group_id = gr.group_id
        AND grole.status = true
        JOIN role_entity ro
        ON ro.role_id = grole.role_id
        AND ro.name = #{roleName}
    """)
    fun findRolePermission(roleName:String, userId: Int, areaSubId: Int ): List<PermissionEntity>?

}