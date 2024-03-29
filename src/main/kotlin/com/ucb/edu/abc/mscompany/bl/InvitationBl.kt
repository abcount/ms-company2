package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.InvitationDao
import com.ucb.edu.abc.mscompany.dto.request.NewInvitationDto
import com.ucb.edu.abc.mscompany.dto.response.InvitationDto
import com.ucb.edu.abc.mscompany.entity.AccessPersonEntity
import com.ucb.edu.abc.mscompany.entity.InvitationEntity
import com.ucb.edu.abc.mscompany.entity.pojos.PersonalInvitations
import com.ucb.edu.abc.mscompany.enums.InvitationState
import com.ucb.edu.abc.mscompany.exception.InvitationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class InvitationBl @Autowired constructor(
    private val invitationDao: InvitationDao,
    private val imageService: ImageService
) {
    fun getInvitationByCompanyAndState(companyId: Int, invitationState: InvitationState): List<InvitationEntity> {
        return invitationDao.getInvitationsByCompanyAndStatus(companyId = companyId, invStatus = invitationState.name)
            ?: throw Exception("Null list of invitations")
    }
    fun convertDtoInvitation(invitationEntity: InvitationEntity, accessPersonEntity: AccessPersonEntity, userId: Int): InvitationDto {
        return InvitationDto(
            invitationId = invitationEntity.invitationId,
            invitedId = userId.toLong(),
            invited = accessPersonEntity.firstName +" "+ accessPersonEntity.lastName,
            email = accessPersonEntity.email,
            urlProfilePicture = imageService.getImageForUser(accessPersonEntity.accessPersonId.toInt())
        )
    }

    fun createInvitation(inv: NewInvitationDto, companyId: Int, creator:Int) {
        val newInvitationEntity = InvitationEntity(
            userId = creator,
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

    fun getPersonalInvitationsByAccessPersonAndState(
        accessPersonEntity: AccessPersonEntity,
        category: InvitationState
    ): List<PersonalInvitations>? {

        return invitationDao.getInvitationsByAccessPersonIdAndListOfCategories(
            accessPersonId = accessPersonEntity.accessPersonId.toInt(),
            cat = category.name
        );
    }

    fun changeStateOfInvitation(invitationId: Int, state:InvitationState) {
        invitationDao.updateStateByInvitationId(invitationId, state.name)
    }
    fun findById(invitationId:Int): InvitationEntity {
        return invitationDao.findById(invitationId)
            ?: throw Exception("Invitation not found $invitationId")
    }

    fun isThereAnCurrentInvitation(
        inv: NewInvitationDto,
        companyId: Int,
        token: String,
        accessPersonEntity: AccessPersonEntity
    ) {

        val listIs =  invitationDao.getInvitationByCatAndAccessIdAndCompanyId(accessPersonId = accessPersonEntity.accessPersonId.toInt(),
            cat = InvitationState.PENDING.name, companyId = companyId)
        if(listIs == null){
            println("THIS LIST IS NULL")
            return
        }
        if(listIs.isEmpty()){
            println("THIS LIST IS EMPTY")
            return
        }
        throw InvitationException("HAS Other invitations");

    }
}