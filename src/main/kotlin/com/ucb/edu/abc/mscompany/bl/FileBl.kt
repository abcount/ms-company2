package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.entity.ReportEntity
import com.ucb.edu.abc.mscompany.enums.UserAbcCategory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.sql.Timestamp
import java.util.UUID

@Service
class FileBl @Autowired constructor(
    private val minioBl: MinioBl,
    private val reportBl: ReportBl,
    private val userBl: UserBl
){
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun generatePDFFile(byteArray: ByteArray, type: String, companyId: Int, header: Map<String, String>, typeReport: String ): String{
        val tokenAuth =  header["authorization"]!!.substring(7)
        val userId = userBl.getUserIdByCompanyIdAndToken (tokenAuth, companyId, UserAbcCategory.ACTIVE,null)
        val uuid = UUID.randomUUID().toString() + companyId + "-" + userId
        val url = minioBl.uploadFile(byteArray, uuid, type)
        val reportEntity = reportBl.factoryReportEntity(header, companyId, uuid, "PDF", typeReport)
        reportBl.createReport(reportEntity)
        logger.info("Se genero el archivo $uuid")
        return url
    }

    suspend fun uploadImage(multipartFile: MultipartFile): String{
        val uuid = UUID.randomUUID().toString()
        minioBl.uploadFile(multipartFile.bytes, uuid, multipartFile.contentType!!)
        logger.info("Se genero el archivo $uuid")
        return uuid
    }
}