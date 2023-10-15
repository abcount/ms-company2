package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.PermissionDao
import com.ucb.edu.abc.mscompany.entity.AccessPersonEntity
import com.ucb.edu.abc.mscompany.entity.InvitationEntity
import com.ucb.edu.abc.mscompany.entity.PermissionEntity
import com.ucb.edu.abc.mscompany.enums.GroupCategory
import com.ucb.edu.abc.mscompany.enums.UserAbcCategory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PermissionBl @Autowired constructor(
    private val groupBl: GroupBl,
    private val permissionDao: PermissionDao,
    private val userBl: UserBl,

){

    /**
     * Get permissions by params
     */



    // create permissions
    /**
     * create permission for any type
     * @param groupCat  for user type in company
     * @param companyId  company id where user must be associated
     * @param tokenAuth is token string from header to get access person uuid{
     * @param listOfAreasSubsidiary array where is stores all id of area subsidiary to insert into permissions
     */
    fun createPermissionsForCompany(tokenAuth: String, listOfAreasSubsidiary: MutableList<Int>, companyId: Int, groupCat: GroupCategory) {
        if(groupCat == GroupCategory.FOUNDER){
            // create user entity because when creating a new company there isn't a user registry in Db, only access person that's why we use token auth
            val userEntity =  userBl.createUserByToken(tokenAuth, category = UserAbcCategory.ACTIVE.name)
                ?: throw Exception("Couldn't create User entity #PermissionBl.createPermissionsForCompany ")

            println("USER ENTITY ======= $userEntity")
            // must create a group of founder with all privileges
            val groupId = groupBl.getOrCreateGroupIDByCat(
                category = GroupCategory.FOUNDER,
                companyId = companyId,
                description = "This is the group of founder with all privileges",
                name = "Fundadores"
            ) ?: throw Exception("Couldn't create GROUP entity #PermissionBl.createPermissionsForCompany ")

            // create permissions on abc_permissions table iterating list
            listOfAreasSubsidiary.forEach { areaSubId ->
                // create permission
                val permissionId = createPermissionsOnDb(areaSubId =  areaSubId, userId = userEntity.userId, status = true)

                // create relationship between abc_permission and group
                val groupPermission = createGroupPermissionOnDB(permissionId = permissionId!!, groupId = groupId)
                    ?: throw Exception("Couldn't create GROUP PERMISSION")
            }

        }

    }

    fun createPermissionsOnDb(areaSubId: Int, userId: Int, status:Boolean): Int? {
        try{
            val map = HashMap<String, Any>()
            map["permission_id"] = 0
            permissionDao.createPermission(areaSubId = areaSubId, status = status ,  userId= userId, dicCategory = "NEW", map = map)
            if (map["permission_id"] == 0){
                throw Exception("Couldn't create PERMISSION #PermissionBl.createPermissionsOnBd")
            }

            return (map["permission_id"] as Int?)!!
        }catch (ex: Exception){
            return null
        }
    }

    fun createGroupPermissionOnDB(permissionId: Int, groupId: Int): Int?{
        try{
            val map = HashMap<String, Int>()
            map["group_access_entity_id"] = 0

            permissionDao.createGroupPermission(groupId = groupId, permissionId = permissionId, map);
            if (map["group_access_entity_id"] == 0){
                throw Exception("Couldn't create PERMISSION #PermissionBl.createGroupPermissionOnDB")
            }
            return map["group_access_entity_id"]!!
        }catch (ex: Exception){
            return null
        }
    }

    fun updateCredentials(token:String, invitation: InvitationEntity, accessPersonEntity:AccessPersonEntity){
        // updateCredentials
        val userEntity = userBl.getUserByCompanyIdAndToken(token, invitation.companyId, UserAbcCategory.INACTIVE ,  accessPersonEntity);
        userBl.updateUserCategory(userEntity, UserAbcCategory.ACTIVE);

        // update permission table
        val perms = permissionDao.getPermissionsByUserAndStatus(userId = userEntity.userId, status = false);
        perms.forEach { perm ->
            permissionDao.updateStatusByPermissionId(true, perm.permissionId)
        }
    }
    fun updatePersonalUpdatedInvitations(token:String, invitationId: Int, accepted: Boolean): MutableMap<String, List<Any>> {
        val accessPersonEntity = userBl.getUserInformationByToken(token)
            ?: throw Exception("NOt access person entity founded")
        val invitation = userBl.getPersonalUpdatedInvitations(token, invitationId, accepted,accessPersonEntity )
            ?: return userBl.getPersonalInvitations(token ,accessPersonEntity)

        updateCredentials(token, invitation, accessPersonEntity);



        return userBl.getPersonalInvitations(token ,accessPersonEntity)
    }



}