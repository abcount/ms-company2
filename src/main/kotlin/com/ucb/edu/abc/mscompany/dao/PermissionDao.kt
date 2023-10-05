package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.GroupEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Component
import java.util.Optional

@Mapper
@Component
interface PermissionDao {

    @Insert("INSERT INTO abc_permission (area_subsidiary_id, user_id , status, dic_category ) " +
            " VALUES ( #{areaSubId}, #{userId}, true, #{dicCategory} ) RETURNING abc_permission_id;")
    @Options(useGeneratedKeys = true, keyProperty = "map.abc_permission_id")
    fun createPermission(areaSubId: Int, userId: Int, dicCategory:String,  @Param("map") map: HashMap<String, Any>)

    @Insert("INSERT INTO group_access_entity (group_id, date_created , abc_permission_id ) " +
            " VALUES ( #{groupId}, now() , #{permissionId} ) RETURNING group_access_entity_id;")
    @Options(useGeneratedKeys = true, keyProperty = "map.group_access_entity_id")
    fun createGroupPermission(groupId: Int, permissionId: Int,  @Param("map") map: HashMap<String, Int>)
}