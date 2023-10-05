package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.CompanyDao
import com.ucb.edu.abc.mscompany.dto.request.CompanyDto
import com.ucb.edu.abc.mscompany.dto.request.CreateCompanyDto
import com.ucb.edu.abc.mscompany.dto.request.EnterpriseDto
import com.ucb.edu.abc.mscompany.entity.AccountEntity
import com.ucb.edu.abc.mscompany.entity.CompanyEntity
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.apache.ibatis.exceptions.PersistenceException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.sql.SQLException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class CompanyBl @Autowired constructor(
        private val companyDao: CompanyDao,
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
        companyEntity.emailRepresentative = companyDto.emailRepresentative
        companyEntity.numberRepresentative = companyDto.numberRepresentative
        companyEntity.legalRepresentative = companyDto.nameRepresentative
        companyEntity.ciRepresentative = companyDto.ciRepresentative
        companyEntity.numberRegistration = companyDto.numberRegistration
        companyEntity.numberEmployee = companyDto.numberEmployee
        companyEntity.rubro = companyDto.rubro
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

    /*
    fun getCompanyById(companyId: Int): EnterpriseDto {
        try {
            val companyEntity = companyDao.getCompanyById(companyId)
            return EnterpriseDto(
                    companyEntity.companyName,
                    companyEntity.diccCategory,
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
    }*/


}
