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
            """
            INSERT INTO company (
                company_name, dicc_category, nit, address, logo_uuid, 
                opening_date, email_representative, number_representative, legal_representative,
                ci_representative, number_registration, number_employee, rubro, status
            ) 
            VALUES (
                #{companyName}, #{diccCategory}, #{nit}, #{address}, #{logoUuid}, #{openingDate}, 
                #{emailRepresentative}, #{numberRepresentative}, #{legalRepresentative},
                #{ciRepresentative}, #{numberRegistration}, #{numberEmployee}, #{rubro}, #{status}
            )
            """
    )
    fun create(company: CompanyEntity)

    @Select("SELECT * FROM company WHERE company_id = #{companyId}")
    fun getCompanyById(companyId: Int): CompanyEntity

    @Update(
            """
            UPDATE company SET 
                company_name = #{companyName}, dicc_category = #{diccCategory}, nit = #{nit}, address = #{address}, 
                logo_uuid = #{logoUuid}, opening_date = #{openingDate}, email_representative = #{emailRepresentative}, 
                number_representative = #{numberRepresentative}, legal_representative = #{legalRepresentative},
                ci_representative = #{ciRepresentative}, number_registration = #{numberRegistration}, 
                number_employee = #{numberEmployee}, rubro = #{rubro}
            WHERE company_id = #{companyId}
            """
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
        and us.dicc_category = #{cate}
        group by c.company_id, c.company_name, us.user_id;
    """)
    fun getCompanyByUserId(accessPersonUuid: String, cate: String): List<CompanyIdAndUserId>


    @Select("SELECT company_name FROM company WHERE company_id = #{companyId}")
    fun getNameCompany(companyId: Int): String

    // get Company status by companyId
    @Select("SELECT status FROM company WHERE company_id = #{companyId}")
    fun getStatusCompany(companyId: Int): Boolean

    // update status by companyId
    @Update("UPDATE company SET status = #{status} WHERE company_id = #{companyId}")
    fun updateStatusCompany(companyId: Int, status: Boolean)



}