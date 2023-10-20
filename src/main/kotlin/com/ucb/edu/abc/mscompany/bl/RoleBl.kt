package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.RoleDao
import com.ucb.edu.abc.mscompany.dto.response.RoleSimpleDto
import com.ucb.edu.abc.mscompany.entity.RoleEntity
import com.ucb.edu.abc.mscompany.enums.RolesAbc
import com.ucb.edu.abc.mscompany.enums.UserAbcCategory
import com.ucb.edu.abc.mscompany.exception.AbcRoleNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.management.relation.Role

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
                commonId = 0
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
                ?: throw AbcRoleNotFoundException("Role $name  not found");
            return roleEntity
        }catch (ex1: AbcRoleNotFoundException){
            println("Something wrong with :RoleBl.getRole ${ex1.message}")
            return null
        }catch (ex: Exception){
            println("Something wrong with :RoleBl.getRole ${ex.message}")
            return null
        }
    }
    fun getAllRoles(): MutableList<RoleEntity>{
        return roleDao.getAllRoles().toMutableList()
    }

    fun transformRoleEntityToDto(rol: RoleEntity, status:Boolean): RoleSimpleDto{
        return RoleSimpleDto(
            roleId = rol.roleId,
            roleDescription = rol.description,
            roleShortName = rol.name,
            status = status,
        )
    }

    fun getAllRolesSimpleDto(): List<RoleSimpleDto> {
        val listComplex = getAllRoles();
        return listComplex.map { it ->
            RoleSimpleDto(
                roleId = it.roleId,
                roleDescription = it.description,
                roleShortName = it.name
            )
        }
    }

    fun getAllRolesFromEnum(): MutableList<RoleEntity> {
        var listCurrent:MutableList<RoleEntity> = mutableListOf()
        val listOfAllRoleInDb = getAllRoles();
        if(listOfAllRoleInDb.size != RolesAbc.values().size) {
            val listOfRolesLocal = RolesAbc.values()
            for (localEnum in listOfRolesLocal) {
                var currentRole: RoleEntity? = null;
                for (roleDb in listOfAllRoleInDb) {
                    if (localEnum.name == roleDb.name) {
                        println("Current rolea jas found in db ${roleDb.name}")
                        currentRole = roleDb;
                    }
                }
                // has found a role or not
                if (currentRole == null) {
                    val roleFromDb = createRole(
                        name = localEnum.name,
                        description = localEnum.descriptionDb,
                        category = "ROLE_RANDOM"
                    )
                        ?: throw Exception("Could not save in Db correctly")
                    // now was created the role in DB
                    currentRole = roleFromDb
                }
                listCurrent.add(currentRole)
            }
        }else{
            listCurrent = listOfAllRoleInDb
        }
        return listCurrent
    }

    fun getRolesByUserIdAndCategoryAndStatusOfRole(userId: Int, userAbcCategory: UserAbcCategory, roleStatus:Boolean): List<RoleEntity> {
        return roleDao.getRolesByUserAndUserCategory(dicCategoryUser = userAbcCategory.name, statusGroupRole = roleStatus, userId=userId)
            ?: throw Exception("Not roles founded")
    }

    fun getMergedCurrentRolesOfUserAndRolesOfCompany(userId: Int, companyId: Int): MutableList<RoleSimpleDto> {
        val listOfCurrentRoles = getRolesByUserIdAndCategoryAndStatusOfRole(userId= userId, userAbcCategory = UserAbcCategory.ACTIVE, roleStatus = true);
        val listOfAllRoles = getAllRolesFromEnum();
        val listOfSimpleRoleDtoResponse : MutableList<RoleSimpleDto> = mutableListOf()
        val listOfCurrentRolesIds = listOfCurrentRoles.map { it.roleId }
        listOfAllRoles.forEach{

            listOfSimpleRoleDtoResponse.add(
                transformRoleEntityToDto(
                    rol = it,
                    status = (listOfCurrentRolesIds.contains(it.roleId))
                )
            )
        }
        return listOfSimpleRoleDtoResponse

    }

}