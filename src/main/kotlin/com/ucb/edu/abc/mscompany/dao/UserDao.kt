package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.AccessPersonEntity
import com.ucb.edu.abc.mscompany.entity.UserEntity
import com.ucb.edu.abc.mscompany.entity.pojos.UserAndAccessPersonInformation
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.springframework.stereotype.Component
import java.util.*

@Mapper
@Component
interface UserDao {

    @Select(
        "SELECT u.user_id, u.access_person_id, u.dicc_category, u.date_created  "+
        " FROM abc_user u , access_person ap WHERE "+
        " ap.access_person_id = u.access_person_id " +
        " AND ap.user_uuid = #{userUuid} ;"
    )
    fun findUsersByAccessPersonUuid(userUuid: String): MutableList<UserEntity>

    @Select(
        "SELECT * from abc_user WHERE user_id = #{userId} "
    )
    fun findById(userId: Int):Optional<UserEntity>
    @Select("SELECT * FROM abc_user WHERE access_person_id = #{accessPersonId} ;")
    fun findByAccessPersonByUuid(userUuid: String): Optional<AccessPersonEntity>

    // must include status
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    @Insert("INSERT INTO abc_user (access_person_id, dicc_category, date_created, status )" +
            " VALUES (#{accessPersonId}, #{diccCategory}, #{dateCreated} , #{status});")
    fun save(userEntity: UserEntity)

    @Select("""
        select us.user_id , acp.first_name, acp.last_name , acp.email
        from 
            company c,
            subsidiary sub, 
            area_subsidiary arsub, 
            abc_permission perm,
            area ar,
            abc_user us,
            access_person acp
        where 
            c.company_id = ar.company_id
        and	sub.company_id = c.company_id
        and	arsub.area_id = ar.area_id
        and	sub.subsidiary_id = arsub.subsidiary_id 
        and	arsub.area_subsidiary_id = perm.area_subsidiary_id
        and perm.user_id = us.user_id
        and acp.access_person_id = us.access_person_id
        and perm.status = true 
        and c.company_id = #{companyId}
        and us.status = true
        AND us.dicc_category = #{category}
        group by us.user_id,  acp.first_name, acp.last_name , acp.email;
    """)
    fun getUserInfoByCompanyIdAndCategory(companyId: Int, category: String): List<UserAndAccessPersonInformation>?


    @Select("""
        select us.user_id , acp.first_name, acp.last_name , acp.email
        from 
            company c,
            subsidiary sub, 
            area_subsidiary arsub, 
            abc_permission perm,
            area ar,
            abc_user us,
            access_person acp
        where 
            c.company_id = ar.company_id
        and	sub.company_id = c.company_id
        and	arsub.area_id = ar.area_id
        and	sub.subsidiary_id = arsub.subsidiary_id 
        and	arsub.area_subsidiary_id = perm.area_subsidiary_id
        and perm.user_id = us.user_id
        and acp.access_person_id = us.access_person_id
        and c.company_id = #{companyId}
        and us.status = true
        and acp.user_uuid = #{userUuid}
        and us.dicc_category = #{cat}
        group by us.user_id,  acp.first_name, acp.last_name , acp.email;
    """)
    fun getUserEntityByCompanyAndAccessPersonUUID(companyId: Int, userUuid: String, cat:String):MutableList<UserAndAccessPersonInformation>?;

    @Update("""
        UPDATE abc_user
        SET dicc_category = #{category}
        WHERE user_id = #{userId}
    """)
    fun updateUserCategory(userId:Int, category:String)

    @Update("""
        UPDATE abc_user
        SET dicc_category = #{category},
        status = #{status}
        WHERE user_id = #{userId}
        
    """)
    fun updateUserStatusAndCategory(userId:Int, category:String, status: Boolean)


    @Select("""
        select us.user_id , acp.first_name, acp.last_name , acp.email
        from 
            company c,
            subsidiary sub, 
            area_subsidiary arsub, 
            abc_permission perm,
            area ar,
            abc_user us,
            access_person acp
        where 
            c.company_id = ar.company_id
        and	sub.company_id = c.company_id
        and	arsub.area_id = ar.area_id
        and	sub.subsidiary_id = arsub.subsidiary_id 
        and	arsub.area_subsidiary_id = perm.area_subsidiary_id
        and perm.user_id = us.user_id
        and acp.access_person_id = us.access_person_id
        and c.company_id = #{companyId}
        and us.status = true
        and acp.user_uuid = #{userUuid} 
        group by us.user_id,  acp.first_name, acp.last_name , acp.email;
    """)
    fun getUserEntityByCompanyAndAccessPersonUuidNOCat(companyId: Int, userUuid: String):MutableList<UserAndAccessPersonInformation>?;

    @Select("""
        select us.user_id , acp.first_name, acp.last_name , acp.email
        from 
            company c,
            subsidiary sub, 
            area_subsidiary arsub, 
            abc_permission perm,
            area ar,
            abc_user us,
            access_person acp
        where 
            c.company_id = ar.company_id
        and	sub.company_id = c.company_id
        and	arsub.area_id = ar.area_id
        and	sub.subsidiary_id = arsub.subsidiary_id 
        and	arsub.area_subsidiary_id = perm.area_subsidiary_id
        and perm.user_id = us.user_id
        and acp.access_person_id = us.access_person_id
        and c.company_id = #{companyId}
        and us.status = true
        and us.user_id = #{userId} 
        group by us.user_id,  acp.first_name, acp.last_name , acp.email;
    """)
    fun getUserEntityByCompanyAndUserIdNOCat(companyId: Int, userId: Int):MutableList<UserAndAccessPersonInformation>?;
}