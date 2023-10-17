package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.EntityDao
import com.ucb.edu.abc.mscompany.entity.EntityEntity
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class EntityBl @Autowired constructor(
    private val entityDao: EntityDao
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createEntity(entityEntity: EntityEntity){
        try {
            logger.info("Creando entidad")
            entityDao.create(entityEntity)
        } catch (e: Exception) {
            logger.error("Error al crear entidad", e)
            throw PostgresException("Error al crear entidad", e.message.toString())
        }
    }

    fun getAllEntitiesByCompanyId(companyId: Int): List<EntityEntity>{
        try {
            logger.info("Obteniendo entidades por id de compañia")
            return entityDao.getEntityByCompanyId(companyId)
        } catch (e: Exception) {
            logger.error("Error al obtener entidades por id de compañia", e)
            throw PostgresException("Error al obtener entidades por id de compañia", e.message.toString())
        }
    }

    fun getEntityById(entityId: Int){
        try {
            logger.info("Obteniendo entidad por id")
            entityDao.getEntityById(entityId)
        } catch (e: Exception) {
            logger.error("Error al obtener entidad por id", e)
            throw PostgresException("Error al obtener entidad por id", e.message.toString())
        }
    }

    fun updateEntity(entityEntity: EntityEntity){
        try {
            logger.info("Actualizando entidad")
            entityDao.update(entityEntity)
        } catch (e: Exception) {
            logger.error("Error al actualizar entidad", e)
            throw PostgresException("Error al actualizar entidad", e.message.toString())
        }
    }

}