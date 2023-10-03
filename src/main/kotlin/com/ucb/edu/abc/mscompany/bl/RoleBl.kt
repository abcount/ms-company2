package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.RoleDao
import com.ucb.edu.abc.mscompany.entity.RoleEntity
import com.ucb.edu.abc.mscompany.exception.AbcRoleNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class RoleBl @Autowired constructor(
    private val roleDao: RoleDao
){
    /**
     * @param name is type of role ei. 'can_do_abc', '***'
     * @param description describes the role ie. 'this user can .....'
     * @param category gives more details like 'FOUNDER' not mandatory
     */
    fun createRole(name: String, description: String, category: String): RoleEntity? {

        try{
            val roleEntity = RoleEntity(
                name = name,
                description = description,
                diccCategory = category,
                dateCreated = LocalDate.now(),
                status = true,
                commomId = 0
            )
            roleDao.createRole(roleEntity);
            return roleEntity
        }catch (ex: Exception){
            println("Something wrong with :RoleBl.createRole ${ex.message}")
            return null
        }

    }

    /**
     * @param name is type of role ei. 'can_do_abc', '***'
     */
    fun getRole(name: String): RoleEntity? {
        try {
            val roleEntity = roleDao.getRoleByName(name)
                .orElseThrow { AbcRoleNotFoundException("Role $name  not found") }
            return roleEntity
        }catch (ex1: AbcRoleNotFoundException){
            println("Something wrong with :RoleBl.getRole ${ex1.message}")
            return null
        }catch (ex: Exception){
            println("Something wrong with :RoleBl.getRole ${ex.message}")
            return null
        }
    }
}