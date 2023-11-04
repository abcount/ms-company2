package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.ReportDao
import com.ucb.edu.abc.mscompany.entity.ReportEntity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReportBl @Autowired constructor(
    private val reportDao: ReportDao
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
}