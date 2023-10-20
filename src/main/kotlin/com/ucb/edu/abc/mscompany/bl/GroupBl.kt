package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.GroupDao
import com.ucb.edu.abc.mscompany.entity.GroupEntity
import com.ucb.edu.abc.mscompany.entity.TestEntity
import com.ucb.edu.abc.mscompany.enums.GroupCategory
import com.ucb.edu.abc.mscompany.enums.RolesAbc
import com.ucb.edu.abc.mscompany.exception.AbcGroupNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class GroupBl @Autowired constructor(
    private val groupDao: GroupDao,
    private val roleBl: RoleBl
) {
    fun getGroupId(category: GroupCategory, companyId: Int): Int? {
        try{
            val groupEntity = groupDao.getGroupIDByCategoryAndCompany(category = category.name, companyId = companyId)
                    .orElseThrow { Exception("Not Found") }
            return groupEntity

        }catch (ex: Exception){
            return null
        }
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

    fun getOrCreateGroupIDByCat(category: GroupCategory, companyId: Int,description: String, name: String ): Int? {
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


                // end of creation
                return hasGroupFounder

            }

            return null
        }catch (abx: AbcGroupNotFoundException){
            return null
        }catch (ex: Exception){
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




}