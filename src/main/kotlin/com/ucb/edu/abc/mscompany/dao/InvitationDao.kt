package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.CompanyEntity
import com.ucb.edu.abc.mscompany.entity.InvitationEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component

@Mapper
@Component
interface InvitationDao {

    @Select("""
        SELECT 
        inv.invitation_id, inv.user_id, inv.company_id, inv.invitation_status, inv.status, 
        inv.access_person_id
        FROM 
        invitation inv,
        company com,
        access_person ap
        WHERE 
            com.company_id = inv.company_id
        AND inv.status = true
        AND ap.access_person_id = inv.access_person_id
        AND com.company_id = #{companyId}
        AND inv.invitation_status = #{invStatus}
    """)
    fun getInvitationsByCompanyAndStatus(companyId: Int, invStatus: String):List<InvitationEntity>?

    @Options(useGeneratedKeys = true, keyProperty = "invitationId")
    @Insert(
        """
            INSERT INTO invitation (
               user_id, company_id, invitation_status, status, access_person_id
            ) 
            VALUES (
                #{userId}, #{companyId}, #{invitationStatus} , true, #{accessPersonId}
            )
            """
    )
    fun createInvitation(invitation: InvitationEntity)
}