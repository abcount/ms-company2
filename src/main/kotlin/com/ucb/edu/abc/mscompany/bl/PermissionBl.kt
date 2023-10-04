package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.PermissionDao
import com.ucb.edu.abc.mscompany.entity.PermissionEntity
import com.ucb.edu.abc.mscompany.enums.GroupCategory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PermissionBl @Autowired constructor(
    private val groupBl: GroupBl,
    private val permissionDao: PermissionDao,
    private val userBl: UserBl,

){

    // FIXME bad request
    fun createNewPermission(permissionEntity: PermissionEntity): Int? {
        val permissionId = permissionDao.createPermission(
            areaSubId = permissionEntity.areaSubsidiaryId!!,
            userId = permissionEntity.userId!!,
            dicCategory = ""
        ).orElseThrow { Exception("Couldnt create permission") }
        return permissionId
    }


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
            val userEntity =  userBl.createUserByToken(tokenAuth, category = GroupCategory.FOUNDER.name)
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
                val permissionId = createPermissionsOnDb(areaSubId =  areaSubId, userId = userEntity.userId)

                // create relationship between abc_permission and group
                val groupPermission = createGroupPermissionOnDB(permissionId = permissionId, groupId = groupId)
                    ?: throw Exception("Couldn't create GROUP PERMISSION")
            }

        }
    }
    fun createPermissionsOnDb(areaSubId: Int, userId: Int): Int{
        val permissionId = permissionDao.createPermission(areaSubId = areaSubId, userId= userId, dicCategory = "NEW")
            .orElseThrow { Exception("Couldn't create PERMISSION #PermissionBl.createPermissionsOnBd") }
        return permissionId
    }

    fun createGroupPermissionOnDB(permissionId: Int, groupId: Int): Int?{
        val groupAccessEntityId = permissionDao.createGroupPermission(groupId = groupId, permissionId = permissionId)
            .orElseThrow { Exception("Couldn't create PERMISSION #PermissionBl.createGroupPermissionOnDB") }
        return groupAccessEntityId
    }




}