package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.UserDao
import com.ucb.edu.abc.mscompany.entity.AccessPersonEntity
import com.ucb.edu.abc.mscompany.entity.UserEntity
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
    private val accessPersonService: AccessPersonBl
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


    // create permissions
    /**
     * create permission for any type
     * @param type  for user type in company
     * @param companyId  company id where user must be associated
     * @param tokenAuth is token string from header to get access person uuid{
     * @param listOfAreasSubsidiaryIds array where is stores all id of area subsidiary to insert into permissions
     */
    fun createPermission(tokenAuth: String, listOfAreasSubsidiaryIds : MutableList<Int>, companyId: Int, type: String){
        if (type == "FOUNDER"){
            val accessPersonEntity = accessPersonService.getAccessPersonInformationByToken(tokenAuth)
                ?: throw Exception("Access Person Service NOt founded")
            val userEntity = createUserByAccessEntity(accessPersonEntity, "FOUNDER")
                ?: throw Exception(" Couldn't create user from access person entity")


        }



    }
    private fun createPermissionWithUser(userEntity: UserEntity, listOfAreasSubsidiaryIds: MutableList<Int>)

    fun createUserByAccessEntity(accessPersonEntity: AccessPersonEntity, category: String):UserEntity? {
        val userEntity = UserEntity()
        userEntity.dateCreated = LocalDate.now()
        userEntity.accessPersonId = accessPersonEntity.accessPersonId.toInt()
        userEntity.diccCategory = category
        userDao.save(userEntity);
        return  userEntity

    }


    fun getUserById(id:Int):UserEntity?{
        try{
            return userRepository.findById(id)
                .orElseThrow { UserNotFoundException("User Not Found Exception") }
        }catch (ex: UserNotFoundException){
            return null
        }

    }
}