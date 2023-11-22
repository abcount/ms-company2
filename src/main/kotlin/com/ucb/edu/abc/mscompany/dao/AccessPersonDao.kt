package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.AccessPersonEntity
import com.ucb.edu.abc.mscompany.entity.CompanyEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.springframework.stereotype.Component
import java.util.*

@Mapper
@Component
interface AccessPersonDao {
    @Select("SELECT * FROM access_person WHERE user_uuid = #{userUuid}")
    fun findByAccessPersonByUuid(userUuid: String): Optional<AccessPersonEntity>

    @Select("SELECT * FROM access_person WHERE access_person_id = #{accessPersonId}")
    fun findById(accessPersonId: Int): Optional<AccessPersonEntity>

    @Options(useGeneratedKeys = true, keyProperty = "accessPersonId")
    @Insert(
        "INSERT INTO access_person " +
                "(username, email, secret , address, no_fono , " +
                "ext_no_fono , country_identity, no_identity, ext_no_identity, first_name, " +
                " last_name, gender_person, birthday, dicc_category, date_creation, " +
                " user_uuid ) " +
                "VALUES " +
                "(#{username}, #{email}, #{secret}, #{address}, #{noFono}, " +
                " #{extNoFono}, #{countryIdentity}, #{noIdentity}, #{extNoIdentity}, #{firstName}, " +
                " #{lastName}, #{genderPerson}, #{birthday}, #{diccCategory}, now(), " +
                " #{userUuid} ) RETURNING access_person_id;")
    fun save(accessPersonEntity: AccessPersonEntity)

    @Select("""
        SELECT * FROM access_person WHERE
         username LIKE #{searched}
         OR email LIKE #{searched}
         LIMIT #{limit}
    """)
    fun findUserByUsernameOrEmail(searched: String, limit:Int): List<AccessPersonEntity>?

    @Update("""
        UPDATE access_person
        SET 
        address = #{address},
        birthday =  #{birthday},
        first_name = #{firstName},
        last_name = #{lastName},
        gender_person = #{genderPerson},
        no_fono = #{noFono},
        ext_no_fono = #{extNoFono},
        country_identity = #{countryIdentity},
        no_identity = #{noIdentity},
        ext_no_identity = #{extNoIdentity}
        WHERE
        access_person_id = #{accessPersonId}
    """)
    fun updateInformation(accessPersonEntity: AccessPersonEntity)
}