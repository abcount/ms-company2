package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.CompanyEntity
import com.ucb.edu.abc.mscompany.entity.pojos.CompanyIdAndUserId
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
import org.springframework.stereotype.Component

@Mapper
@Component
interface CompanyDao {

    @Options(useGeneratedKeys = true, keyProperty = "companyId")
    @Insert(
            "INSERT INTO company (company_name, dicc_category, nit, address, logo_uuid, " +
                    "opening_date, deadline, contact_email, contact_name) VALUES (#{companyName}, " +
                    "#{diccCategory}, #{nit}, #{address}, #{logoUuid}, #{openingDate}, now(), " +
                    "#{contactEmail}, #{contactName})")
    fun create(company: CompanyEntity)

    @Select("SELECT * FROM company WHERE company_id = #{companyId}")
    fun getCompanyById(companyId: Int): CompanyEntity

    @Update(
            "UPDATE company SET company_name = #{companyName}, dicc_category = #{diccCategory}, " +
                    "nit = #{nit}, address = #{address}, logo_uuid = #{logoUuid}, " +
                    "contact_email = #{contactEmail}, contact_name = #{contactName} " +
                    "WHERE company_id = #{companyId}"
    )
    fun updateCompany(company: CompanyEntity)

    @Select("""
        select c.company_id , c.company_name , us.user_id
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
        and acp.user_uuid = #{accessPersonUuid}
        and us.status = true
        group by c.company_id, c.company_name, us.t_user_id;
    """)
    fun getCompanyByUserId(accessPersonUuid: String): List<CompanyIdAndUserId>





}