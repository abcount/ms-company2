package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.CompanyEntity
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



}