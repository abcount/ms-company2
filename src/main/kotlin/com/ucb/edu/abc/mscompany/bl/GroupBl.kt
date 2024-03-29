package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.GroupDao
import com.ucb.edu.abc.mscompany.entity.AccessPersonEntity
import com.ucb.edu.abc.mscompany.entity.GroupEntity
import com.ucb.edu.abc.mscompany.entity.RoleEntity
import com.ucb.edu.abc.mscompany.entity.TestEntity
import com.ucb.edu.abc.mscompany.entity.pojos.GroupRoleExtendedPojo
import com.ucb.edu.abc.mscompany.enums.GroupCategory
import com.ucb.edu.abc.mscompany.enums.RolesAbc
import com.ucb.edu.abc.mscompany.exception.AbcGroupNotFoundException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class GroupBl @Autowired constructor(
    private val groupDao: GroupDao,
    private val roleBl: RoleBl
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    fun getGroupId(category: GroupCategory, companyId: Int): Int? {
        try{
            val groupEntity = groupDao.getGroupIDByCategoryAndCompany(category = category.name, companyId = companyId)
                    .orElseThrow { Exception("Not Found") }
            return groupEntity

        }catch (ex: Exception){
            return null
        }
    }

    fun getGroupEntityByUser(userId: Int): GroupEntity{
        return groupDao.getGroupByUserId(userId)
            ?: throw Exception("Not group founded for user id $userId")
    }
    fun createGroup(category: GroupCategory, companyId: Int, description: String, name: String): GroupEntity? {
        try {
            val groupEntity = GroupEntity(
                name= name,
                description = description,
                commonId = companyId,
                diccCategory = category.name,
                status = true,
                dateCreated = LocalDate.now()
            )
            groupDao.createGroup(groupEntity)
            return groupEntity
        }catch (ex: Exception){
            return null
        }
    }

    fun getOrCreateGroupIDByCat(category: GroupCategory, companyId: Int,description: String, name: String, tokenAuth:String , accessPerson: AccessPersonEntity? ): Int? {
        try{
            if (category == GroupCategory.FOUNDER){
                var hasGroupFounder = getGroupId(GroupCategory.FOUNDER, companyId)
                if (hasGroupFounder == null){
                    val groupEntity =  createGroup(category, companyId, description, name) ?: throw Exception("Couldnt create")
                    hasGroupFounder = groupEntity.groupId
                }
                // create association of roles and group
                var roleEntity = roleBl.getAllRolesFromEnum();
                if (roleEntity.size != RolesAbc.values().size){
                    for( rol in RolesAbc.values()){
                        val roleEntity2 = roleBl.getRole(rol.name)
                        if(roleEntity2 == null){
                            val flagRole = roleBl.createRole(rol.name, rol.descriptionDb, "ANYONE")
                                ?: throw Exception("Could not create role")
                            roleEntity.add(flagRole)
                        }
                    }

                }

                for(rol in roleEntity){

                    createGroupRole(rol.roleId, hasGroupFounder, true)
                        ?: throw Exception("Error")


                }

                // create role in kc for company
                GlobalScope.launch {
                    roleBl.createRoleWithCompany(companyId, roleEntity, accessPerson, tokenAuth);
                }



                // end of creation
                return hasGroupFounder

            }

            return null
        }catch (abx: AbcGroupNotFoundException){
            return null
        }catch (ex: Exception){
            logger.error(ex.message);
            return null
        }
    }

    fun createGroupRole(roleId: Int, groupId: Int, status:Boolean): Int? {
        try{
            // really helpful doc > https://stackoverflow.com/questions/59668117/how-to-properly-use-the-param-annotation-of-mybatis/59811574#59811574

            val map = HashMap<String, Any>()
            map["group_role_id"] = 0
            val grId = groupDao.createRoleGroup(TestEntity(roleId, groupId, status), map)
            return map["group_role_id"] as Int?

        }catch (ex: Exception){
            return null
        }
    }

    fun checkFirstArrayInSecondArray(first: List<Int>, second: List<Int>){
        first.forEach{
            if(! second.contains(it)){
                throw Exception("first not In second");
            }
        }
    }
    fun createRoleGroupByList(roleIdListWhiteList: List<Int>, allRolesList: List<Int>, groupId:Int ){
        checkFirstArrayInSecondArray(roleIdListWhiteList, allRolesList);
        allRolesList.forEach{ role ->

            val res = createGroupRole(
                roleId = role,
                groupId = groupId,
                status = roleIdListWhiteList.contains(role)
            )
                ?: throw Exception("Could not save role group id in groupRole Table")
        }
    }

    fun getRolesByGroupId(groupId:Int): List<GroupRoleExtendedPojo> {
        return groupDao.getRolesByGroupId(groupId = groupId)
    }



    fun updateRolesWhenAccepted(userId: Int, companyId: Int,  accessPersonEntity: AccessPersonEntity?, token: String){
        val listOfRoles =roleBl.getRolesByUserNoFilters(userId, roleStatus = true);
        roleBl.createRoleWithCompany(companyId, listOfRoles, accessPersonEntity, token)
    }

    fun updateRolesForThisGroup(groupEntityOfPerson: GroupEntity, roles: MutableList<Int>) {
        val currentRolesOfUser = getRolesByGroupId(groupEntityOfPerson.groupId)
        currentRolesOfUser.forEach {
            if(roles.contains(it.roleId)){
                if(!it.status){
                    groupDao.updateGroupRoleById(groupRoleId = it.groupRoleId, status = true)
                }
            }else{
                if(it.status){
                    groupDao.updateGroupRoleById(groupRoleId = it.groupRoleId, status = false)
                }
            }
        }
    }

    fun updateRolesForThisGroupInKC(
        groupEntityOfPerson: GroupEntity, roles: MutableList<Int>, companyId: Int,
        accessPerson: AccessPersonEntity, currentRoles: List<GroupRoleExtendedPojo>
    ) {
        val createList: MutableList<RoleEntity> = mutableListOf()
        val deleteList: MutableList<RoleEntity> = mutableListOf()
        println("*******")
        println(roles)
        println("---------------------------------------")
        println(currentRoles.map { it.roleId })
        println("*******")
        currentRoles.forEach {
            if (roles.contains(it.roleId)) {
                if (!it.status) {
                    // created
                    createList.add(
                        roleBl.getRoleById(it.roleId)
                    )
                    println(it.name)
                }
            } else {
                if (it.status) {
                    deleteList.add(
                        roleBl.getRoleById(it.roleId)
                    )
                    println(it.name)
                }
            }
        }

        println("---------------------------------------")

        roleBl.removeRoleWithCompany(
            companyId = companyId,
            roles = deleteList, accessPersonEntity = accessPerson, null
        );
        roleBl.createRoleWithCompany(
            companyId = companyId,
            roles = createList, accessPersonEntity = accessPerson, null
        )
    }



}