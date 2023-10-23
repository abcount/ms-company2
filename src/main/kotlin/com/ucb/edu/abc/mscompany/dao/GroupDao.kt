package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.GroupEntity
import com.ucb.edu.abc.mscompany.entity.RoleEntity
import com.ucb.edu.abc.mscompany.entity.TestEntity
import com.ucb.edu.abc.mscompany.entity.pojos.GroupRoleExtendedPojo
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.HashMap

@Mapper
@Component
interface GroupDao {

    @Select("SELECT group_id FROM group_entity WHERE dicc_category = #{nameGroup} AND common_id = #{companyId}")
    fun getGroupIDByCategoryAndCompany(category: String, companyId: Int): Optional<Int>

    @Select("SELECT * FROM group_entity WHERE companyId = #{companyId}")
    fun getAllGroupByCompany(companyId: Int): Optional<MutableList<GroupEntity>>


    @Options(useGeneratedKeys = true, keyProperty = "groupId")
    @Insert("INSERT INTO group_entity (name, description, dicc_category, status, date_created, common_id) " +
            " VALUES (#{name}, #{description}, #{diccCategory}, #{status}, #{dateCreated}, #{commonId});")
    fun createGroup(groupEntity: GroupEntity)



    @Insert("INSERT INTO group_role (role_id, group_id, status, date_created) " +
            " VALUES (#{entidad.roleId}, #{entidad.groupId}, #{entidad.status}, now() )  ;")
    @Options(useGeneratedKeys = true, keyProperty = "map.group_role_id")
    fun createRoleGroup(@Param("entidad") entity: TestEntity, @Param("map") vals: HashMap<String, Any>): Int?
    // this  piece of code doesn't work with mybatis, great font: https://kamalmeet.com/java/returning-auto-generated-id-in-spring-mybatis/
    // so i made a trick using map instead object entity


    @Select("""
        SELECT DISTINCT g.* 
        FROM group_entity g
        JOIN group_access_entity gperm
        ON gperm.group_id  = g.group_id
        JOIN abc_permission perm
        ON perm.permission_id = gperm.abc_permission_id
        JOIN abc_user usr
        ON usr.user_id = perm.user_id
        AND usr.user_id = #{userId}
    """)
    fun getGroupByUserId(userId: Int): GroupEntity?

    @Update("""
        UPDATE group_role 
        SET status = #{status}
        WHERE group_role_id = #{groupRoleId}
    """)
    fun updateGroupRoleById(groupRoleId: Int, status: Boolean)


    @Select("""
    SELECT grr.group_role_id, grr.role_id, grr.group_id, r.name , grr.status
    FROM group_role grr
    JOIN role_entity r
    ON r.role_id = grr.role_id
    JOIN group_entity g
    ON g.group_id = grr.group_id
    WHERE g.group_id = #{groupId}
    """)
    fun getRolesByGroupId(groupId: Int): List<GroupRoleExtendedPojo>

}