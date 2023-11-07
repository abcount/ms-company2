package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.ReportDao
import com.ucb.edu.abc.mscompany.entity.ReportEntity
import com.ucb.edu.abc.mscompany.enums.UserAbcCategory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ReportBl @Autowired constructor(
    private val reportDao: ReportDao,
    private val userBl: UserBl
){
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createReport(reportEntity: ReportEntity){
        try{
            logger.info("Se creo el reporte ${reportEntity.uuid}")
            reportDao.insertReport(reportEntity)
        }catch (ex: Exception){
            logger.error("Ocurrio un error al crear el reporte ${reportEntity.uuid}")
            throw Exception("Ocurrio un error al crear el reporte ${reportEntity.uuid}")
        }
    }

    fun factoryReportEntity(header: Map<String,String>, companyId: Int, uuid: String, typeDocument:String, typeReport: String): ReportEntity{
        val tokenAuth =  header["authorization"]!!.substring(7)
        val userId = userBl.getUserIdByCompanyIdAndToken (tokenAuth, companyId, UserAbcCategory.ACTIVE,null)
        var reportEntity = ReportEntity()
        reportEntity.companyId = companyId
        reportEntity.uuid = uuid
        reportEntity.typeDocument = typeDocument
        reportEntity.typeReport = typeReport
        reportEntity.userId = userId
        return reportEntity
    }

}