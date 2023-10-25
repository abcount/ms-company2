package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.CompanyEntity
import com.ucb.edu.abc.mscompany.entity.InvitationEntity
import com.ucb.edu.abc.mscompany.entity.pojos.PersonalInvitations
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update
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

    @Select("""
        SELECT 
        inv.invitation_id, ap2.username, com.company_name , inv.invitation_status, inv.status, 
        inv.access_person_id, com.company_id, ap2.username as urlImage
        FROM 
        invitation inv,
        company com,
        access_person ap,
        abc_user usr,
        access_person ap2
        WHERE 
            com.company_id = inv.company_id
        AND ap.access_person_id = inv.access_person_id
        AND usr.user_id = inv.user_id
        AND ap2.access_person_id = usr.access_person_id
        AND inv.status = true
        AND inv.invitation_status = #{cat}
        AND ap.access_person_id = #{accessPersonId}
    """)
    fun getInvitationsByAccessPersonIdAndListOfCategories(accessPersonId : Int, cat: String ):List<PersonalInvitations>?

    @Select("""
        SELECT * 
        FROM invitation
        WHERE invitation_id = #{invitationId}
        AND status = true
    """)
    fun findById(invitationId:Int): InvitationEntity?

    @Update("""
        UPDATE invitation 
        SET invitation_status = #{invitationStatus}
        WHERE invitation_id = #{invitationId}
    """)
    fun updateStateByInvitationId(invitationId: Int, invitationStatus: String )


    @Select("""
        SELECT 
        inv.invitation_id, ap2.username, com.company_name , inv.invitation_status, inv.status, 
        inv.access_person_id
        FROM 
        invitation inv,
        company com,
        access_person ap,
        abc_user usr,
        access_person ap2
        WHERE 
            com.company_id = inv.company_id
        AND ap.access_person_id = inv.access_person_id
        AND usr.user_id = inv.user_id
        AND ap2.access_person_id = usr.access_person_id
        AND inv.status = true
        AND inv.invitation_status = #{cat}
        AND ap.access_person_id = #{accessPersonId}
        AND com.company_id = #{companyId}
    """)
    fun getInvitationByCatAndAccessIdAndCompanyId(accessPersonId : Int, cat: String, companyId: Int ):List<PersonalInvitations>?
}