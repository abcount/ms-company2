package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.GroupEntity
import com.ucb.edu.abc.mscompany.entity.PermissionEntity
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

    @Select("""
        SELECT * FROM abc_permission
        WHERE user_id = #{userId}
        AND status = #{status}
    """)
    fun getPermissionsByUserAndStatus(userId: Int, status: Boolean): List<PermissionEntity>

    @Update("""
        UPDATE abc_permission 
        SET status = #{status}
        WHERE permission_id = #{permissionId}
    """)
    fun updateStatusByPermissionId(status: Boolean, permissionId: Int)
}