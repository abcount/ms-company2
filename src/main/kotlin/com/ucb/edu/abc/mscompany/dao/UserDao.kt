package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.AccessPersonEntity
import com.ucb.edu.abc.mscompany.entity.AccountEntity
import com.ucb.edu.abc.mscompany.entity.UserEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
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

    @Options(useGeneratedKeys = true, keyProperty = "userId")
    @Insert("INSERT INTO abc_user (access_person_id, dicc_category, date_created )" +
            " VALUES (#{accessPersonId}, #{diccCategory}, #{dateCreated} );")
    fun save(userEntity: UserEntity)


}