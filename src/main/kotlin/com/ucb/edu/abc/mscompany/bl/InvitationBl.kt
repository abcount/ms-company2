package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.InvitationDao
import com.ucb.edu.abc.mscompany.dto.request.NewInvitationDto
import com.ucb.edu.abc.mscompany.dto.response.InvitationDto
import com.ucb.edu.abc.mscompany.entity.AccessPersonEntity
import com.ucb.edu.abc.mscompany.entity.InvitationEntity
import com.ucb.edu.abc.mscompany.enums.InvitationState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class InvitationBl @Autowired constructor(
    private val invitationDao: InvitationDao,
) {
    fun getInvitationByCompanyAndState(companyId: Int, invitationState: InvitationState): List<InvitationEntity> {
        return invitationDao.getInvitationsByCompanyAndStatus(companyId = companyId, invStatus = invitationState.name)
            ?: throw Exception("Null list of invitations")
    }
    fun convertDtoInvitation(invitationEntity: InvitationEntity, accessPersonEntity: AccessPersonEntity): InvitationDto {
        return InvitationDto(
            invitationId = invitationEntity.invitationId,
            invitedId = accessPersonEntity.accessPersonId,
            invited = accessPersonEntity.firstName +" "+ accessPersonEntity.lastName,
            email = accessPersonEntity.email,
            urlProfilePicture = "https://bestprofilepictures.com/wp-content/uploads/2021/04/Cool-Picture-Wallpaper-473x1024.jpg"
        )
    }

    fun createInvitation(inv: NewInvitationDto, companyId: Int) {
        val newInvitationEntity = InvitationEntity(
            userId = 0,
            companyId = companyId,
            invitationStatus = InvitationState.PENDING.name,
            status = true,
            accessPersonId = inv.userId
        )
        invitationDao.createInvitation(newInvitationEntity)
        if(newInvitationEntity.invitationId == 0){
            throw Exception("Can not create invitation")
        }
    }
}