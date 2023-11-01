package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.RoleDao
import com.ucb.edu.abc.mscompany.dao.RoleKcDao
import com.ucb.edu.abc.mscompany.dto.response.RoleSimpleDto
import com.ucb.edu.abc.mscompany.dto.response.RolesKcDto
import com.ucb.edu.abc.mscompany.entity.AccessPersonEntity
import com.ucb.edu.abc.mscompany.entity.RoleEntity
import com.ucb.edu.abc.mscompany.entity.RolesKcEntity
import com.ucb.edu.abc.mscompany.enums.RolesAbc
import com.ucb.edu.abc.mscompany.enums.UserAbcCategory
import com.ucb.edu.abc.mscompany.exception.AbcRoleNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID
import javax.management.relation.Role

@Service
class RoleBl @Autowired constructor(
    private val roleDao: RoleDao,
    private val keycloakBl: KeycloakBl,
    private val roleKcDao: RoleKcDao
){
    private val defaultRoles: List<String> = listOf("offline_access", "uma_authorization", "default-roles-abcount")
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

    fun getAllRolesByUserId(userId: Int): List<RoleEntity> {
        return roleDao.getRolesByUser(userId)
            ?: throw Exception("Not roles founded for $userId")
    }


    fun getRolesByUserNoFilters(userId:Int, roleStatus: Boolean): List<RoleEntity> {
        return roleDao.getRolesByUserAndUserCategoryNOFilters( statusGroupRole = roleStatus, userId=userId)
            ?: throw Exception("Not roles founded")
    }

    fun getMergedCurrentRolesOfUserAndRolesOfCompany(userId: Int, companyId: Int): MutableList<RoleSimpleDto> {
        val listOfCurrentRoles = getRolesByUserNoFilters(userId= userId, roleStatus = true);
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

    fun createOrPutRoleInKc(roleName: String, description: String): RolesKcDto {
        var getRoleFromKc = keycloakBl.getRoleInKc(roleName)
        if(getRoleFromKc?.id == null){
            keycloakBl.createRoleInKc(name = roleName, description = description)
            getRoleFromKc = keycloakBl.getRoleInKc(roleName)
                ?: throw Exception("Role couldnt be created")
        }
        return getRoleFromKc;

    }

//    fun createOrUpdateThisRolesForThisUser(roles: List<RoleEntity>, userUuid: String, token: String){
//        val currentListOfRoles = keycloakBl.currentRolesInToken(token);
//        val rolesDesired = roles.map { it.name }
//        val toDelete = currentListOfRoles.filter { it !in rolesDesired }
//        val toAdd = rolesDesired.filter { it !in currentListOfRoles }
//        for
//    }
//    fun createRoleByCompanyIdAndUserUuidInKc(roleName: String, description: String, userUuid: String){
//
//        val roleKc = createOrPutRoleInKc(roleName, description )
//        keycloakBl.createRoleInKc()
//    }

    fun removeRoleWithCompany(companyId: Int, roles:List<RoleEntity>, accessPersonEntity: AccessPersonEntity?, token: String){
        if(accessPersonEntity == null) throw Exception("Null person");
        val listOfMaps: MutableList<Map<String, String>> = mutableListOf()
        for(role in roles){
            val mapRes = checkRole(companyId, role, token);
            if(mapRes != null){
                listOfMaps.add(mapRes)
            }

            val mapRes2 = checkRole(0, role, token);
            if(mapRes2 != null){
                listOfMaps.add(mapRes2)
            }
        }
        keycloakBl.removeRoleInKcToUserUuid(accessPersonEntity.userUuid, listOfMaps)

    }

    fun getOrCreateRoleInLocalDB(role:String, companyId: Int, roleDesc:String, singleRole:String): RolesKcEntity {
        val listRole =
            roleKcDao.getRolesByRoleNameAndCompanyId(role, companyId ) ?: throw Exception("Null or empty role kc");
        if(listRole.size > 1) throw Exception("Duplicated role with company Id")

        if(listRole.isEmpty()){
            // should create
            keycloakBl.createRoleInKc(role, roleDesc);
            val roleInKc = keycloakBl.getRoleInKc(role)
                ?:throw Exception("Couldn't found role $role in KC DB")
            val roleKcEntity = RolesKcEntity(roleKcId = 0,
                uuidRole = roleInKc.id!!,
                companyId = companyId,
                completeRole = roleInKc.name!!,
                roleName = singleRole)
            roleKcDao.createRoleInKc(roleKcEntity);
            return roleKcEntity;
        }else{
            // found something
            return listRole[0]
        }
    }

    fun createRoleWithCompany(companyId: Int, roles:List<RoleEntity>, accessPersonEntity: AccessPersonEntity?, token: String){
        if(accessPersonEntity == null) throw Exception("Null person");
        val listOfMaps: MutableList<Map<String, String>> = mutableListOf()
        for(role in roles){
            val mapRes = checkRole(companyId, role, token);
            if(mapRes != null){
                listOfMaps.add(mapRes)
            }

            val mapRes2 = checkRole(0, role, token);
            if(mapRes2 != null){
                listOfMaps.add(mapRes2)
            }
        }
        keycloakBl.assignRoleInKcToUserUuid(accessPersonEntity.userUuid, listOfMaps)


    }
    fun checkRole(companyId: Int, role:RoleEntity,  token: String): Map<String, String>? {
        var currentRole = role.name
        if(companyId != 0) {
            currentRole = "${role.name}.$companyId"
        }
        // check if current role is in token
        if( ! keycloakBl.isThisRoleInCurrentToken(token, currentRole) ){
            // role is not present in token
            val roleInDb =  getOrCreateRoleInLocalDB(currentRole, companyId, role.description, role.name);
            return mapOf(
                "name" to roleInDb.completeRole,
                "id" to roleInDb.uuidRole
            )


        }
        return null;
    }



}