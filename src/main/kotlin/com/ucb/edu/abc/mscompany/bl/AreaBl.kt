package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.AreaDao
import com.ucb.edu.abc.mscompany.entity.AreaEntity
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.apache.ibatis.exceptions.PersistenceException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.SQLException
import java.util.Date

@Service
class AreaBl @Autowired constructor(
    private val areaDao: AreaDao
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun create(areaEntity: AreaEntity): Int{
        try{
            logger.info("Creando area")
            areaDao.create(areaEntity)
            return areaEntity.areaId
        } catch (e: Exception){
            throw PostgresException("Ocurrio un error al crear la area: ${areaEntity.toString()}", e.message.toString())
        }
    }

    fun factoryArea(name: String, companyId: Int, commonId: Int?): AreaEntity{
        val areaEntity = AreaEntity()
        areaEntity.companyId = companyId
        areaEntity.areaName = name
        areaEntity.dateCreated = Date()
        areaEntity.status = true
        areaEntity.commonId = commonId
        return areaEntity
    }

}