package com.ucb.edu.abc.mscompany.bl


import com.ucb.edu.abc.mscompany.dao.CompanyDao
import com.ucb.edu.abc.mscompany.dao.FileDao
import com.ucb.edu.abc.mscompany.dto.request.CompanyDto
import com.ucb.edu.abc.mscompany.dto.request.EnterpriseDto
import com.ucb.edu.abc.mscompany.dto.request.NewInvitationDto
import com.ucb.edu.abc.mscompany.dto.response.CompanyListDto
import com.ucb.edu.abc.mscompany.entity.AccessPersonEntity
import com.ucb.edu.abc.mscompany.entity.CompanyEntity
import com.ucb.edu.abc.mscompany.entity.pojos.FileEntity
import com.ucb.edu.abc.mscompany.enums.GroupCategory
import com.ucb.edu.abc.mscompany.enums.InvitationState
import com.ucb.edu.abc.mscompany.enums.UserAbcCategory
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.apache.ibatis.exceptions.PersistenceException

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter



@Service
class CompanyBl @Autowired constructor(
    private val companyDao: CompanyDao,
    private val userBl: UserBl,
    private val fileDao: FileDao,
    private val invitationBl: InvitationBl,
    private val permissionBl: PermissionBl,
    private val groupBl: GroupBl,
    private val roleBl: RoleBl,
    private val imageService: ImageService
) {
    @Value("\${server.port}")
    lateinit var port: String

    private val logger: Logger = LoggerFactory.getLogger(CompanyBl::class.java)



    fun create(companyEntity: CompanyEntity): Int {
        try{
            logger.info("Creando compañia")

            companyDao.create(companyEntity)
            return companyEntity.companyId
        } catch (e: Exception){
            throw PostgresException("Ocurrio un error al crear la compañia: ${companyEntity.toString()}", e.message.toString())
        }

    }

    fun get(companyId: Int): CompanyEntity{
        try{
            logger.info("Obteniendo compañia")
            return companyDao.getCompanyById(companyId)
        } catch (e: Exception){
            //TODO: lanzar error 404
            throw PostgresException("Ocurrio un error al obtener la compañia con el id: $companyId", e.message.toString())
        }
    }

    fun factoryCompany(companyDto: CompanyDto, formatDate: String, image: MultipartFile): CompanyEntity {
        val companyEntity = CompanyEntity()
        companyEntity.companyName = companyDto.enterpriseName
        companyEntity.diccCategory = "enabled"
        companyEntity.nit = companyDto.nit
        companyEntity.address = companyDto.enterpriseLocation
        //TODO: convertir a Base64 o guardar en un Bucket y asignar el valor
        companyEntity.logoUuid = convertMultipartFileToByteArray(image)
        companyEntity.emailRepresentative = companyDto.emailRepresentative
        companyEntity.numberRepresentative = companyDto.numberRepresentative
        companyEntity.legalRepresentative = companyDto.nameRepresentative
        companyEntity.ciRepresentative = companyDto.ciRepresentative
        companyEntity.numberRegistration = companyDto.numberRegistration
        companyEntity.numberEmployee = companyDto.numberEmployee
        companyEntity.rubro = companyDto.rubro
        companyEntity.status= true
        companyEntity.openingDate = LocalDate.parse(companyDto.openingDate, DateTimeFormatter.ofPattern(formatDate))
        return companyEntity
    }

    fun convertMultipartFileToByteArray(file: MultipartFile): ByteArray? {
        try {
            return file.bytes
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    fun getCompanyEntityById(companyId:Int): CompanyEntity {
        return companyDao.getCompanyById(companyId)
    }

    fun getCompanyById(companyId: Int): EnterpriseDto {
        try {
            val companyEntity = companyDao.getCompanyById(companyId)
            return EnterpriseDto(
                    companyEntity.companyName,
                    companyEntity.nit,
                    companyEntity.address,
                    companyEntity.logoUuid,
                    companyEntity.emailRepresentative,
                    companyEntity.numberRepresentative,
                    companyEntity.legalRepresentative,
                    companyEntity.ciRepresentative,
                    companyEntity.numberRegistration,
                    companyEntity.numberEmployee,
                    companyEntity.rubro
            )
        } catch (e: PersistenceException) {
            logger.error("Ocurrio un error al obtener la compañia con el id: $companyId error: ${e.message.toString()}")
            throw PostgresException("Ocurrio un error al obtener la compañia con el id: $companyId", e.message.toString())
        }
    }

    fun getCompanyName(companyId: Int): String {
        try{
            logger.info("Obteniendo nombre de compañia")
            return companyDao.getNameCompany(companyId)
        } catch (e: Exception){
            logger.error("Ocurrio un error al obtener el nombre de la compañia con el id: $companyId")
            throw PostgresException("Ocurrio un error al obtener el nombre de la compañia con el id: $companyId", e.message.toString())
        }
    }

    fun updateCompany(enterpriseDto: EnterpriseDto, companyId: Int, image: MultipartFile){
        try {
            var companyEntity = companyDao.getCompanyById(companyId)
            companyEntity.companyName = enterpriseDto.companyName
            companyEntity.nit = enterpriseDto.nit
            companyEntity.address = enterpriseDto.address
            companyEntity.logoUuid = convertMultipartFileToByteArray(image)
            println("logo: ${companyEntity.logoUuid}")
            companyEntity.emailRepresentative = enterpriseDto.emailRepresentative
            companyEntity.numberRepresentative = enterpriseDto.numberRepresentative
            companyEntity.legalRepresentative = enterpriseDto.legalRepresentative
            companyEntity.ciRepresentative = enterpriseDto.ciRepresentative
            companyEntity.numberRegistration = enterpriseDto.numberRegistration
            companyEntity.numberEmployee = enterpriseDto.numberEmployee
            companyEntity.rubro = enterpriseDto.rubro
            companyDao.updateCompany(companyEntity)
        } catch (e: PersistenceException) {
            throw PostgresException("Ocurrio un error al actualizar la compañia ", e.message.toString())
        }
    }
// fixme its burned
    fun getCompaniesByAccessPerson(token: String): List<CompanyListDto> {
        val accessPersonEntity = userBl.getUserInformationByToken(token)
            ?: throw Exception("Null accessPersonEntity");
        val listOfCompanies = companyDao.getCompanyByUserId(accessPersonEntity.userUuid, UserAbcCategory.ACTIVE.name)
        val listDtoCompanies = listOfCompanies.map { item->
            CompanyListDto(
                companyId = item.companyId,
                companyName = item.companyName,
                urlIconImage = imageService.getImageFormatCompany(item.companyId), //"http://localhost:$port/image/company/${item.companyId}",
                userId = item.userId)
        }
        return listDtoCompanies
    }

// font > https://stackoverflow.com/questions/69088235/how-to-insert-multipartfile-photo-into-db-as-base64-string-in-java-spring-boot-a
    fun saveThisFileWithId(companyId: Int, image: MultipartFile, category: String) {
        //var bites = Base64.encodeBase64(image.bytes)
        val fileEntity = FileEntity(
            imageContent = image.bytes,
            ownerId = companyId,
            categoryOwner = category,
        )
        fileDao.createImage(fileEntity)
    }



    fun checkIfUserAlreadyExists(companyId: Int, accessPerson: AccessPersonEntity): Boolean {
        return try{
            val usr = userBl.getUserByCompanyIdAndToken("", companyId, UserAbcCategory.ACTIVE, accessPerson);
            true;
        }catch (ex: Exception){
            false
        }
    }

    // invitations
    fun createNewInvitation(inv: NewInvitationDto, companyId: Int, token: String){

        val accessPersonEntity = userBl.getAccessPersonById(inv.userId);

        // check if accessPersonEntityId as userId exists
        invitationBl.isThereAnCurrentInvitation(inv, companyId, token, accessPersonEntity)
        // check if user is already in company
        if(checkIfUserAlreadyExists(companyId, accessPersonEntity)) throw Exception("Usuario ya esta incorporado a la empresa")

        // set invitation
        val userIdOfCreator = userBl.getUserIdByCompanyIdAndToken(token= token , userAbcCategory = UserAbcCategory.ACTIVE, companyId = companyId, currentAccessPersonEntity = null)
        invitationBl.createInvitation(inv, companyId, userIdOfCreator)

        // createCredentials

        // createAUserRelationSHip
        val userEntity = userBl.createUserByAccessPersonId(
            accessPersonId = accessPersonEntity.accessPersonId.toInt(),
            category = UserAbcCategory.INACTIVE.name,
            status = true)

        // create permission

        // create group
        val groupEntity = groupBl.createGroup(category = GroupCategory.EMPLOYEE, companyId = companyId,
            description = "Group EMPLOYEE", name = "employee")
            ?: throw Exception("NUll group")

        // listOfAllRoles()
        // creategroupRole
        groupBl.createRoleGroupByList(
            roleIdListWhiteList = inv.roles,
            allRolesList = roleBl.getAllRolesFromEnum().map { it.roleId },
            groupId = groupEntity.groupId
        )

        /*
        // FIXME DEPRECATED
        @Deprecated
        inv.roles.forEach { rol ->
            val res = groupBl.createGroupRole(rol, groupEntity.groupId, true)
                ?: throw Exception("Couldnt create group role ")
        }

         */
        //create permission
        inv.areaSubsidiaryId.forEach { arSub ->
            val permissionId = permissionBl.createPermissionsOnDb(areaSubId = arSub, status = false ,userId = userEntity.userId)
                ?:throw Exception("Couldnt create permissionId")
            val groupPermissionId = permissionBl.createGroupPermissionOnDB(permissionId = permissionId, groupId = groupEntity.groupId)
                ?:throw Exception("Couldnt create groupPermissionId")
        }

    }


    fun getPermissionAndRolesByUserAndCompany(userId: Int, companyId: Int): Map<String, List<Any>> {
        return mapOf(
            "areasAndSubs" to permissionBl.getPermissionsByUserAndCompanyId(userId, companyId),
            "roles" to roleBl.getMergedCurrentRolesOfUserAndRolesOfCompany(userId, companyId)
        )
    }

    fun updatePermissionsByCompanyAndUserId(requestedChanges: NewInvitationDto, companyId: Int, tokenCreator: String ){
        val groupEntityOfPerson =  groupBl.getGroupEntityByUser(requestedChanges.userId);
        val currentRoles = groupBl.getRolesByGroupId(groupEntityOfPerson.groupId);
        groupBl.updateRolesForThisGroup(groupEntityOfPerson, requestedChanges.roles);
        permissionBl.updatePermissions(companyId = companyId, requestedChanges, groupEntity = groupEntityOfPerson);

        // check if shouldUpdateIn KC
        val userEntity = userBl.getUserById(requestedChanges.userId)
            ?: throw Exception("No user entity founded")
        if(userEntity.diccCategory == UserAbcCategory.ACTIVE.name){

            groupBl.updateRolesForThisGroupInKC(groupEntityOfPerson, requestedChanges.roles, companyId, userBl.getAccessPersonById(userEntity.accessPersonId), currentRoles = currentRoles);
        }
    }

    fun deleteUserByCompany(companyId:Int, userId: Int){
        val userEntity = userBl.getUserInformationByCompanyIdAndUserId(userId, companyId);
        permissionBl.deletePermissionsByUserId(userEntity.employeeId);
        userBl.updateUserStatusAndCategory(userId, UserAbcCategory.DISABLED, status = false);
    }
    fun cancelInvitation(invitationId: Int, userId: Int){
        userBl.updateUserStatusAndCategory(userId, UserAbcCategory.DISABLED, status = false);
        invitationBl.changeStateOfInvitation(invitationId, InvitationState.CANCELED);

    }

}

