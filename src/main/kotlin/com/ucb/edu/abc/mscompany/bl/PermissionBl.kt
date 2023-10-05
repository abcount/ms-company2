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
                val groupPermission = createGroupPermissionOnDB(permissionId = permissionId!!, groupId = groupId)
                    ?: throw Exception("Couldn't create GROUP PERMISSION")
            }

        }
    }
    fun createPermissionsOnDb(areaSubId: Int, userId: Int): Int? {
        try{
            val map = HashMap<String, Any>()
            map["abc_permission_id"] = 0
            permissionDao.createPermission(areaSubId = areaSubId, userId= userId, dicCategory = "NEW", map = map)
            if (map["abc_permission_id"] == 0){
                throw Exception("Couldn't create PERMISSION #PermissionBl.createPermissionsOnBd")
            }

            return (map["abc_permission_id"] as Int?)!!
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




}