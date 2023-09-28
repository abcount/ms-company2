package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.AreaSubsidiaryDao
import com.ucb.edu.abc.mscompany.entity.AreaSubsidiaryEntity
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.apache.ibatis.exceptions.PersistenceException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.SQLException

@Service
class AreaSubsidiaryBl @Autowired constructor(
        private val areaSubsidiaryDao: AreaSubsidiaryDao
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun create(areaSubsidiaryEntity: AreaSubsidiaryEntity): Int {
        try{
            logger.info("Creando area-sucursal")
            areaSubsidiaryDao.create(areaSubsidiaryEntity)
            return areaSubsidiaryEntity.areaSubsidiaryId
        } catch (e: Exception){
            throw PostgresException("Ocurrio un error al crear la area-sucursal: ${areaSubsidiaryEntity.toString()}", e.message.toString())
        }
    }

    fun factoryAreaSubsidiary(areaId:Int, subsidiaryId: Int, diccCategory: String): AreaSubsidiaryEntity{
        val areaSubsidiaryEntity = AreaSubsidiaryEntity()
        areaSubsidiaryEntity.areaId = areaId
        areaSubsidiaryEntity.subsidiaryId = subsidiaryId
        areaSubsidiaryEntity.diccCategory = diccCategory
        return areaSubsidiaryEntity
    }

}