package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.FileDao
import com.ucb.edu.abc.mscompany.dao.UserDao
import com.ucb.edu.abc.mscompany.dto.request.UserUpdateInfoDto
import com.ucb.edu.abc.mscompany.dto.response.AccessPersonWithImageDtoResponse
import com.ucb.edu.abc.mscompany.dto.response.Employee
import com.ucb.edu.abc.mscompany.dto.response.UsersAndInvitation
import com.ucb.edu.abc.mscompany.entity.AccessPersonEntity
import com.ucb.edu.abc.mscompany.entity.InvitationEntity
import com.ucb.edu.abc.mscompany.entity.UserEntity
import com.ucb.edu.abc.mscompany.entity.pojos.UserAndAccessPersonInformation
import com.ucb.edu.abc.mscompany.enums.InvitationState
import com.ucb.edu.abc.mscompany.enums.UserAbcCategory
import com.ucb.edu.abc.mscompany.exception.UserNotFoundException
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
@AllArgsConstructor
@NoArgsConstructor
class UserBl @Autowired constructor(
    private val userDao: UserDao,
    private val accessPersonService: AccessPersonBl,
    private val invitationBl: InvitationBl,
    private val imageService: ImageService
) {
    
    fun getAccessPersonById(accessPersonId: Int): AccessPersonEntity {
        return accessPersonService.getAccessPersonInformationById(accessPersonId)
            ?: throw UserNotFoundException("Access Person Not found");
    }

    /**
     * @param token token of user doing the request
     * @param companyId
     * @param userCat categories of user ex. INACTIVE, ACTIVE
     * @param currentAccessPersonEntity optional if you have already the accessPersonEntity
     * @return returns USERENTITY object
     */
    fun getUserByCompanyIdAndToken(token:String, companyId:Int, userCat: UserAbcCategory, currentAccessPersonEntity: AccessPersonEntity?): UserEntity {
        val accessPersonEntity = getAccessPersonEntityEasy(token, currentAccessPersonEntity)

        val listFilterByCompanyAndAccessPersonUUid =
            userDao.getUserEntityByCompanyAndAccessPersonUUID(
                companyId = companyId,cat=userCat.name, userUuid = accessPersonEntity!!.userUuid)
        
        if(listFilterByCompanyAndAccessPersonUUid.isNullOrEmpty())  
            throw Exception("Could not get list merged")
        if(listFilterByCompanyAndAccessPersonUUid.size > 1){
            throw Exception("More that one user and company");
        }
        val id =  listFilterByCompanyAndAccessPersonUUid[0].userId;
        return getUserById(id)
            ?: throw Exception("Not user found");

    }


    fun getUserByAccessPersonEntityAndCompany(companyId: Int, accessPerson: AccessPersonEntity): UserAndAccessPersonInformation {
        val listOfUser = userDao.getUserEntityByCompanyAndAccessPersonUuidNOCat(companyId = companyId, userUuid = accessPerson.userUuid)
        if(listOfUser.isNullOrEmpty()){
            throw Exception("Not matched company id and access person entity");
        }
        if(listOfUser.size > 1){
            throw Exception("Multiple users")
        }
        return listOfUser[0]
    }
    fun getAccessPersonEntityEasy(token: String, currentAccessPersonEntity: AccessPersonEntity?): AccessPersonEntity? {
        var accessPersonEntity = currentAccessPersonEntity
        if(currentAccessPersonEntity == null){
            accessPersonEntity = accessPersonService.getAccessPersonInformationByToken(token)
                ?: throw Exception("Access person entity not found");
        }
        return accessPersonEntity
    }
    /**
     * @param token token of user doing the request
     * @param companyId
     * @param userAbcCategory categories of user ex. INACTIVE, ACTIVE
     * @param currentAccessPersonEntity optional if you have already the accessPersonEntity
     * @return returns just ID (Integer)
     */
    fun getUserIdByCompanyIdAndToken(token:String, companyId:Int, userAbcCategory: UserAbcCategory, currentAccessPersonEntity: AccessPersonEntity?): Int {
        var accessPersonEntity = currentAccessPersonEntity
        if(currentAccessPersonEntity == null){
            accessPersonEntity = accessPersonService.getAccessPersonInformationByToken(token)
                ?: throw Exception("Access person entity not found");
        }


        val listFilterByCompanyAndAccessPersonUUid = userDao.getUserEntityByCompanyAndAccessPersonUUID(companyId = companyId,cat=userAbcCategory.name, userUuid = accessPersonEntity!!.userUuid)
        if(listFilterByCompanyAndAccessPersonUUid.isNullOrEmpty()) // TODO
           throw Exception("Could not get list merged")
        if(listFilterByCompanyAndAccessPersonUUid.size > 1){
            throw Exception("More that one user and company");
        }
        val id =  listFilterByCompanyAndAccessPersonUUid[0].userId;
        return id;
    }
    fun getUsersListByAccessPerson(accessPersonEntity: AccessPersonEntity): MutableList<UserEntity> {
        return userDao.findUsersByAccessPersonUuid(userUuid = accessPersonEntity.userUuid)
    }
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

    fun getUserInformationForGeneralApi(token: String): AccessPersonWithImageDtoResponse {
        val accessPersonEntity = getUserInformationByToken(token)
            ?: throw Exception("Null object")
        val accessPersonDto = AccessPersonWithImageDtoResponse()
        accessPersonDto.apply {
            accessPersonId = accessPersonEntity.accessPersonId
            username = accessPersonEntity.username
            email = accessPersonEntity.email
            secret = accessPersonEntity.secret
            address = accessPersonEntity.address
            noFono = accessPersonEntity.noFono
            extNoFono = accessPersonEntity.extNoFono
            countryIdentity = accessPersonEntity.countryIdentity
            noIdentity = accessPersonEntity.noIdentity
            extNoIdentity = accessPersonEntity.extNoIdentity
            firstName = accessPersonEntity.firstName
            lastName = accessPersonEntity.lastName
            genderPerson = accessPersonEntity.genderPerson
            birthday = accessPersonEntity.birthday
            diccCategory = accessPersonEntity.diccCategory
            dateCreation = accessPersonEntity.dateCreation
            userUuid = accessPersonEntity.userUuid
            urlImage = imageService.getImageForUser(accessPersonEntity.accessPersonId.toInt())?:""
        }
        return accessPersonDto
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
    fun createUserByAccessPersonId(
        accessPersonId: Int, category: String, status: Boolean): UserEntity {
        val userEntity = UserEntity()
        userEntity.dateCreated = LocalDate.now()
        userEntity.accessPersonId = accessPersonId;
        userEntity.diccCategory = category;
        userEntity.status = status;
        userDao.save(userEntity);
        return userEntity;
    }

    fun createUserByToken(token: String, category: String?): UserEntity? {
        val accessPersonEntity = accessPersonService.getAccessPersonInformationByToken(token)
            ?: throw Exception("Not found")
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

    fun getUserListByCompanyId(companyId:Int): List<UserAndAccessPersonInformation> {
        return userDao.getUserInfoByCompanyIdAndCategory(companyId, category = UserAbcCategory.ACTIVE.name)
            ?: throw Exception("Null list for user by companyId")

    }

    fun  getUserInformationByCompanyIdAndUserId(userId: Int, companyId:Int): Employee {
        val listRes = userDao.getUserEntityByCompanyAndUserIdNOCat(companyId, userId)
        if(listRes.isNullOrEmpty()){
            throw Exception("This is null or empty")
        }
        if(listRes.size > 1) throw Exception("Not Value")
        return Employee(
            employeeId = listRes[0].userId,
            name = "${listRes[0].firstName} ${listRes[0].lastName}",
            email = listRes[0].email,
            urlProfilePicture = imageService.getImageForUser(listRes[0].accessPersonId!!)
        )
    }

    fun getUserInformationInvitationByCompanyId(companyId: Int): UsersAndInvitation{
        val usersAndInvitation = UsersAndInvitation(
            mutableListOf(),
            mutableListOf()
        )
        val userInfoList = getUserListByCompanyId(companyId)
        usersAndInvitation.employee = userInfoList.map { item ->
            Employee(employeeId = item.userId,
                name = "${item.firstName} ${item.lastName}",
                email = item.email,
                urlProfilePicture = imageService.getImageForUser(item.accessPersonId!!))
        }
        val invitationList = invitationBl.getInvitationByCompanyAndState(companyId = companyId, invitationState = InvitationState.PENDING)
        invitationList.forEach { item ->
            val ap = accessPersonService.getAccessPersonInformationById(item.accessPersonId)
            if(ap != null){
                usersAndInvitation.invitation.add(
                    invitationBl.convertDtoInvitation(
                        accessPersonEntity = ap,
                        invitationEntity = item,
                        userId = getUserByAccessPersonEntityAndCompany(companyId = companyId, accessPerson = ap).userId

                    )
                )
            }
        }


        return usersAndInvitation
    }

    fun updateUserCategory(userEntity: UserEntity, userCat: UserAbcCategory){
        userDao.updateUserCategory(userId = userEntity.userId, category = userCat.name)
    }

    fun updateUserStatusAndCategory(userId: Int, userCat: UserAbcCategory, status: Boolean){
        userDao.updateUserStatusAndCategory(userId = userId, category = userCat.name, status = status)
    }
    fun getPersonalInvitations(token: String, currentAccessPersonEntity: AccessPersonEntity?): MutableMap<String, List<Any>> {
        var accessPersonEntity = currentAccessPersonEntity
        if(currentAccessPersonEntity == null){
            accessPersonEntity = accessPersonService.getAccessPersonInformationByToken(token)
                ?: throw Exception("Access person entity not found");
        }

        val personaInvitations = invitationBl.getPersonalInvitationsByAccessPersonAndState(accessPersonEntity!!, InvitationState.PENDING)
            ?: throw Exception("Personal invitation null");
        val personalInvitations2 = invitationBl.getPersonalInvitationsByAccessPersonAndState(accessPersonEntity, InvitationState.REFUSED)
            ?: throw Exception("Personal invitation refused null");

        personaInvitations.forEach {
            it.urlImage = imageService.getImageFormatCompany(it.companyId)
        }
        personalInvitations2.forEach {
            it.urlImage = imageService.getImageFormatCompany(it.companyId)
        }
        //create dto
        val mapToReturn: MutableMap<String, List<Any>> = mutableMapOf()
        mapToReturn[InvitationState.PENDING.name] = personaInvitations;
        mapToReturn[InvitationState.REFUSED.name] = personalInvitations2;
        return mapToReturn;
    }


    // function to get invitation
    fun getPersonalUpdatedInvitations(token: String, invitationId:Int, accepted:Boolean, currentAccessPersonEntity: AccessPersonEntity?): InvitationEntity? {
        var accessPersonEntity = currentAccessPersonEntity
        if(currentAccessPersonEntity == null){
            accessPersonEntity = accessPersonService.getAccessPersonInformationByToken(token)
                ?: throw Exception("Access person entity not found");
        }
        val invitation = invitationBl.findById(invitationId)
        if(invitation.accessPersonId.toLong() != accessPersonEntity?.accessPersonId)
            throw Exception("Not mached in invitation and accessPerson")

        if(accepted){
            invitationBl.changeStateOfInvitation(invitationId, InvitationState.ACCEPTED);
        }else{
            invitationBl.changeStateOfInvitation(invitationId, InvitationState.REFUSED)
            return null
        }
        return invitation

    }

    fun updateUserInfo(userToken: String, updateInfo: UserUpdateInfoDto) {
        val accessPerson = accessPersonService.getAccessPersonInformationByToken(userToken)
            ?: throw Exception("User not found")

        if(! updateInfo.dni.isNullOrBlank()) accessPerson.noIdentity = updateInfo.dni!!.trim()
        if(! updateInfo.birthday.isNullOrBlank()) accessPerson.birthday = LocalDate.parse(updateInfo.birthday!!.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        if(! updateInfo.names.isNullOrBlank()) accessPerson.firstName = updateInfo.names!!.trim()
        if(! updateInfo.lastnames.isNullOrBlank()) accessPerson.lastName = updateInfo.lastnames!!.trim()
        if(! updateInfo.gender.isNullOrBlank()) accessPerson.genderPerson = updateInfo.gender!!.trim().toInt()
        if(! updateInfo.address.isNullOrBlank()) accessPerson.address = updateInfo.address!!.trim()
        if(! updateInfo.phoneNumber.isNullOrBlank()) accessPerson.noFono = updateInfo.phoneNumber!!.trim()
        if(! updateInfo.domainNumber.isNullOrBlank()) accessPerson.extNoFono = updateInfo.domainNumber!!.trim()
        if(! updateInfo.country.isNullOrBlank()) accessPerson.countryIdentity = updateInfo.country!!.trim()
        if(! updateInfo.dniExtension.isNullOrBlank()) accessPerson.extNoIdentity = updateInfo.dniExtension!!.trim()

        // now save data
        accessPersonService.updateAccessPersonInformation(accessPerson)

        if( updateInfo.imageProfile != null){
            // save picture
             imageService.updateUserProfilePicture(userId = accessPerson.accessPersonId.toInt(), image = updateInfo.imageProfile!!)
        }
    }
}
