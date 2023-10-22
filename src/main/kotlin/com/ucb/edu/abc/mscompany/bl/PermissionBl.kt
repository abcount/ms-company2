package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.PermissionDao
import com.ucb.edu.abc.mscompany.dto.request.NewInvitationDto
import com.ucb.edu.abc.mscompany.dto.response.AreaDtoRes
import com.ucb.edu.abc.mscompany.dto.response.SubsidiaryDtoRes
import com.ucb.edu.abc.mscompany.entity.AccessPersonEntity
import com.ucb.edu.abc.mscompany.entity.GroupEntity
import com.ucb.edu.abc.mscompany.entity.InvitationEntity
import com.ucb.edu.abc.mscompany.entity.pojos.SubsidiaryAndAreaPojo
import com.ucb.edu.abc.mscompany.enums.GroupCategory
import com.ucb.edu.abc.mscompany.enums.RolesAbc
import com.ucb.edu.abc.mscompany.enums.UserAbcCategory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PermissionBl @Autowired constructor(
    private val groupBl: GroupBl,
    private val permissionDao: PermissionDao,
    private val userBl: UserBl,
    private val areaSubsidiaryBl: AreaSubsidiaryBl,

){
    private val logger = LoggerFactory.getLogger(this::class.java)
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

    fun getPermissionsByUserId(userId: Int): List<SubsidiaryAndAreaPojo> {
        return permissionDao.findByUserId(userId = userId)
    }
    fun getAreasAndSubsByCompany(companyId: Int): List<SubsidiaryAndAreaPojo> {
        return areaSubsidiaryBl.getAreasSubsidiaryByCompany(companyId = companyId)
    }

    fun getPermissionsByUserAndCompanyId(userId: Int, companyId: Int): List<SubsidiaryDtoRes> {
        val listOfAreaSubsCurrentUser = getPermissionsByUserId(userId)
        val listOfAreaSubsCompany = areaSubsidiaryBl.getAreasSubsidiaryByCompany(companyId = companyId);
        val listOfAreasSubsIdsOfCurrentUser = listOfAreaSubsCurrentUser.map { it.areaSubsidiaryId }
        var mapOfSummarizedValues : MutableMap<Int, SubsidiaryDtoRes> = mutableMapOf();
        var mapOfSizeElements : MutableMap<Int, Int> = mutableMapOf()
        listOfAreaSubsCompany.forEach{ item ->
            val idSub = item.subsidiaryId
            if(mapOfSummarizedValues[idSub] == null){
                mapOfSummarizedValues[idSub] = SubsidiaryDtoRes(
                    subsidiaryId = item.subsidiaryId,
                    subsidiaryName = item.subsidiaryName,
                    areas = mutableListOf()
                )
            }
            val area = AreaDtoRes(
                areaId = item.areaId,
                areaName = item.areaName,
                areaSubsidiaryId = item.areaSubsidiaryId,
                status = false
            )
            if( listOfAreasSubsIdsOfCurrentUser.contains(item.areaSubsidiaryId)){
                area.status = true;
                if(mapOfSizeElements[idSub]== null){
                    mapOfSizeElements[idSub] = 0;
                }
                mapOfSizeElements[idSub] = mapOfSizeElements[idSub]!! + 1;
            }
            mapOfSummarizedValues[idSub]?.areas?.add(area)
        }
        mapOfSizeElements.forEach { t, u ->
            if(u == mapOfSummarizedValues[t]!!.areas.size ){
                mapOfSummarizedValues[t]!!.status = true;
            }
        }
        return mapOfSummarizedValues.values.toList();
    }

    fun convertListAreasSubObjIntoMap(listOfObjects: List<SubsidiaryAndAreaPojo>): MutableMap<Int, SubsidiaryAndAreaPojo> {
        var map: MutableMap<Int, SubsidiaryAndAreaPojo> = mutableMapOf();
        listOfObjects.forEach {
            map[it.areaSubsidiaryId] = it
        }
        return map;
    }
    fun updatePermissions(companyId: Int, requestedChanges: NewInvitationDto, groupEntity: GroupEntity) {

        val currentPermissionsByUser = getPermissionsByUserId(requestedChanges.userId);
        val mapOfCurrentPermissionsByUser = convertListAreasSubObjIntoMap(currentPermissionsByUser)
        val listOfAreasSubsInUser = currentPermissionsByUser.map { it.areaSubsidiaryId }

        val listToDelete = listOfAreasSubsInUser.filterNot{ it in requestedChanges.areaSubsidiaryId}
        // find common
        //val common = listOfAreasSubsInUser.filter { it in requestedChanges.areaSubsidiaryId }
        //val common = listOfAreasSubsInUser.intersect(requestedChanges.areaSubsidiaryId.toSet())
        val listToUpload = requestedChanges.areaSubsidiaryId.filterNot { it in listOfAreasSubsInUser }

        val listPermissionIdToDelete = getPermissionIdFromThisMap(listToDelete, mapOfCurrentPermissionsByUser)

        deleteThisPermissionInGroup(listPermissionIdToDelete, groupEntity.groupId)
        deleteThisPermissions(listPermissionIdToDelete)
        createThisPermissions(listToUpload, requestedChanges.userId, groupEntity.groupId)
    }

    private fun createThisPermissions(listToUploadAreaSub: List<Int>, userId: Int, groupId: Int) {
        listToUploadAreaSub.forEach {
            val permissionId = createPermissionsOnDb(areaSubId = it, userId = userId, status = true)
                ?: throw Exception("Could not be created in DB permission")
            createGroupPermissionOnDB(permissionId = permissionId, groupId = groupId)
                ?: throw Exception("Could not be created in DB permission group")
        }
    }

    private fun deleteThisPermissionInGroup(listDeleted: List<Int>, groupId: Int){
        listDeleted.forEach {
            permissionDao.deleteGroupPermissionsByPermission(it, groupId)
        }
    }
    private fun deleteThisPermissions(listDeleted: List<Int>){
        listDeleted.forEach {
            permissionDao.deletePermissionById(it);
        }
    }
    private fun getPermissionIdFromThisMap(listToDelete: List<Int>, mapOfSubsAreasId: MutableMap<Int, SubsidiaryAndAreaPojo>): MutableList<Int> {
        val listPermissionsIdToDelete : MutableList<Int> = mutableListOf()
        listToDelete.forEach {
            if(mapOfSubsAreasId[it] != null){
                val idPermission = mapOfSubsAreasId[it]?.permissionId;
                if(idPermission != null){
                    listPermissionsIdToDelete.add(idPermission);
                }

            }
        }
        return listPermissionsIdToDelete;
    }

    private fun getUserId(token: String, companyId: Int): Int {
        val user = userBl.getUserByCompanyIdAndToken(token = token, companyId = companyId,
            userCat = UserAbcCategory.ACTIVE, null)
        return user.userId
    }
    private fun getAreaSubIdByCompany(areaId: Int, subsidiary: Int, companyId: Int): Int {
        return areaSubsidiaryBl.getAreaSubIdByCompany(subsidiaryId = subsidiary, areaId = areaId)
            ?: throw Exception("Not founded")
    }

    /**
     * check if user has access to the action
     * @param tokenUser  token of person who access the resource
     * @param companyId  company id where user must be associated
     * @param areaId id of area
     * @param subsidiary id of sub
     * @param role must be the action performed in resource
     * @param companyId id of company
     * @param userId (OPTIONAL default null) when request has userId, it saves request to get user
     * @param areaSubId (OPTIONAL default null) when request has area subsidiary id, it saves request to get sub id
     *
     * @return boolean true if exists role and false if not
     */
    fun canPerformThisAction(tokenUser: String, areaId:Int, subsidiary:Int,
                             role: RolesAbc, companyId: Int,
                             userId: Int? = null, areaSubId: Int? = null ): Boolean {
        val currentUserId = userId ?: getUserId(tokenUser, companyId)
        val currentAreaSubId = areaSubId ?: getAreaSubIdByCompany(areaId, subsidiary, companyId)

        val listOfResult = permissionDao.findRolePermission(role.name, currentUserId, currentAreaSubId);
        if(listOfResult.isNullOrEmpty()){
            logger.error("$currentUserId can not access to ${role.name} with area sub $currentAreaSubId")
            return false;
        }
        if(listOfResult.size > 1){
            logger.error("$currentUserId has more access to ${role.name} with area sub $currentAreaSubId")
            return false;
        }
        return true;
    }


}