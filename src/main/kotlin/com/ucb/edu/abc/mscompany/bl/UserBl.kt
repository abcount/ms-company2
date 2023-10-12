package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.UserDao
import com.ucb.edu.abc.mscompany.dto.response.Employee
import com.ucb.edu.abc.mscompany.dto.response.UsersAndInvitation
import com.ucb.edu.abc.mscompany.entity.AccessPersonEntity
import com.ucb.edu.abc.mscompany.entity.UserEntity
import com.ucb.edu.abc.mscompany.enums.InvitationState
import com.ucb.edu.abc.mscompany.exception.UserNotFoundException
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@AllArgsConstructor
@NoArgsConstructor
class UserBl @Autowired constructor(
    private val userDao: UserDao,
    private val accessPersonService: AccessPersonBl,
    private val invitationBl: InvitationBl
) {
    fun getUserInformationByToken(token: String): AccessPersonEntity? {
        try{
            val accessPersonEntity = accessPersonService.getAccessPersonInformationByToken(token)
                ?: throw Exception("Couldnt found access person in local db neither in kc db")
            return accessPersonEntity;
        } catch (ex: UserNotFoundException) {
            return null;
        }catch (ex2: Exception){
            return null;
        }
    }
    fun getUserInformationByAccessPersonUuid(accessPersonUuid: String): AccessPersonEntity {
        val accessPersonEntity = accessPersonService.getAccessPersonInformationByUuid(accessPersonUuid)
            ?: throw Exception("Couldn't")
        return accessPersonEntity

    }

    fun createUserByAccessEntity(accessPersonEntity: AccessPersonEntity, category: String):UserEntity? {
        val userEntity = UserEntity()
        userEntity.dateCreated = LocalDate.now()
        userEntity.accessPersonId = accessPersonEntity.accessPersonId.toInt()
        userEntity.diccCategory = category
        userEntity.status = true
        userDao.save(userEntity);
        return  userEntity

    }

    fun createUserByToken(token: String, category: String?): UserEntity? {
        val accessPersonEntity = accessPersonService.getAccessPersonInformationByToken(token)
            ?: throw Exception("NOt found")
        val catFinal  = category?:""
        return createUserByAccessEntity(accessPersonEntity, catFinal)
    }


    fun getUserById(id:Int):UserEntity?{
        try{
            return userDao.findById(id)
                .orElseThrow { UserNotFoundException("User Not Found Exception") }
        }catch (ex: UserNotFoundException){
            return null
        }

    }

    fun getUserInformationByCompanyId(companyId: Int): UsersAndInvitation{
        val usersAndInvitation = UsersAndInvitation(
            mutableListOf(),
            mutableListOf()
        )
        val userInfoList = userDao.getUserInfoByCompanyId(companyId)
            ?: throw Exception("Null list for user by companyId")
        usersAndInvitation.employee = userInfoList.map { item ->
            Employee(employeeId = item.userId,
                name = "${item.firstName} ${item.lastName}",
                email = item.email,
                urlProfilePicture = "")
        }
        val invitationList = invitationBl.getInvitationByCompanyAndState(companyId = companyId, invitationState = InvitationState.PENDING)
        invitationList.forEach { item ->
            val ap = accessPersonService.getAccessPersonInformationById(item.accessPersonId);
            if(ap != null){
                usersAndInvitation.invitation.add(
                    invitationBl.convertDtoInvitation(
                        accessPersonEntity = ap,
                        invitationEntity = item
                    )
                )
            }
        }


        return usersAndInvitation
    }
}