package com.ucb.edu.abc.mscompany.bl


import com.ucb.edu.abc.mscompany.dao.CompanyDao
import com.ucb.edu.abc.mscompany.dao.FileDao
import com.ucb.edu.abc.mscompany.dto.request.CompanyDto
import com.ucb.edu.abc.mscompany.dto.request.EnterpriseDto
import com.ucb.edu.abc.mscompany.entity.CompanyEntity
import com.ucb.edu.abc.mscompany.entity.pojos.FileEntity
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.apache.ibatis.exceptions.PersistenceException
import org.apache.tomcat.util.codec.binary.Base64
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter



@Service
class CompanyBl @Autowired constructor(
        private val companyDao: CompanyDao,
    private val userBl: UserBl,
    private val fileDao: FileDao
) {
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
        companyEntity.contactEmail = companyDto.contactMail
        companyEntity.contactName = companyDto.contactName
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

    fun getCompanyById(companyId: Int): EnterpriseDto {
        try {
            val companyEntity = companyDao.getCompanyById(companyId)
            return EnterpriseDto(
                    companyEntity.companyName,
                    companyEntity.diccCategory,
                    companyEntity.nit,
                    companyEntity.address,
                    companyEntity.logoUuid,
                    companyEntity.contactEmail,
                    companyEntity.contactName
            )

        } catch (e: PersistenceException) {
            throw PostgresException("Ocurrio un error al obtener la compañia con el id: $companyId", e.message.toString())
        }
    }

    fun updateCompany(enterpriseDto: EnterpriseDto, companyId: Int){
        try {
            var companyEntity = companyDao.getCompanyById(companyId)

            companyEntity.companyName = enterpriseDto.companyName
            companyEntity.diccCategory = enterpriseDto.diccCategory
            companyEntity.nit = enterpriseDto.nit
            companyEntity.address = enterpriseDto.address
            companyEntity.logoUuid = enterpriseDto.logoUuid
            companyEntity.contactEmail = enterpriseDto.contactEmail
            companyEntity.contactName = enterpriseDto.contactName
            companyDao.updateCompany(companyEntity)
        } catch (e: PersistenceException) {
            throw PostgresException("Ocurrio un error al actualizar la compañia ", e.message.toString())
        }
    }

    fun getCompaniesByAccessPerson(token: String){
        val accessPersonEntity = userBl.getUserInformationByToken(token)
            ?: throw Exception("Null accessPersonEntity");
        val listOfCompanies = companyDao.getCompanyByUserId(accessPersonEntity.userUuid)
    }
    fun getImageOfCompany(id: Int): ByteArray? {
        var string =  companyDao.getCompanyById(id)
        return string.logoUuid
    }
// font > https://stackoverflow.com/questions/69088235/how-to-insert-multipartfile-photo-into-db-as-base64-string-in-java-spring-boot-a
    fun saveThisFileWithId(companyId: Int, image: MultipartFile, category: String) {
        var bites = Base64.encodeBase64(image.bytes)
        val fileEntity = FileEntity(
            imageContent = bites,
            ownerId = companyId,
            categoryOwner = category,
        )
        fileDao.createImage(fileEntity)
    }

    fun getImageOfCompany2(id: Int): String {
        var string =  fileDao.getImageByIdAndCategory(categoryOwner ="COMPANY-PROFILE-IMAGE", ownerId = id)
        println(string.length)
        println("This wouldbe an image 15 ${string.substring(0, 15)}")
        println("This. wouldbe an image -15 ${string.substring(string.length - 16, string.length)}" )

        return string
    }


}
