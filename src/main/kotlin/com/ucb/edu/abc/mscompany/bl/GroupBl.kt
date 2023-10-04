package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.GroupDao
import com.ucb.edu.abc.mscompany.entity.GroupEntity
import com.ucb.edu.abc.mscompany.enums.GroupCategory
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
                    val groupEntity =   createGroup(category, companyId, description, name) ?: throw Exception("Couldnt create")
                    hasGroupFounder = groupEntity.groupId
                }
                // create association of roles and group
                val roleEntity = roleBl.getRole("***")
                    ?: roleBl.createRole("***", "this is role is for founders", GroupCategory.FOUNDER.name )
                    ?: throw Exception("ERROR")
                createGroupRole(roleEntity.roleId, hasGroupFounder)
                    ?: throw Exception("Error")
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


    fun createGroupRole(roleId: Int, groupId: Int): Int? {
        try{
            val grId = groupDao.createRoleGroup(roleId, groupId)
//            if(grId.isPresent){
//                return grId.get()
//            }
            return grId

        }catch (ex: Exception){
            return null
        }
    }


}