package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.AuxiliaryAccountDao
import com.ucb.edu.abc.mscompany.dto.response.Auxiliary
import com.ucb.edu.abc.mscompany.entity.AuxiliaryAccountEntity
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class AuxiliaryAccountBl @Autowired constructor(
    private val auxiliaryAccountDao: AuxiliaryAccountDao
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createAuxiliaryAccount(auxiliaryAccountEntity: AuxiliaryAccountEntity): AuxiliaryAccountEntity{
        try {
            logger.info("Creando cuenta auxiliar")
            auxiliaryAccountDao.create(auxiliaryAccountEntity)
            return auxiliaryAccountEntity
        } catch (e: Exception) {
            logger.error("Error al crear cuenta auxiliar", e)
            throw PostgresException("Error al crear cuenta auxiliar", e.message.toString())
        }
    }

    fun getAuxiliariesAccountByCompanyId(companyId: Int): List<AuxiliaryAccountEntity>{
        try {
            logger.info("Obteniendo cuentas auxiliares por id de compañia")
            return auxiliaryAccountDao.getAuxiliaryAccountByCompanyId(companyId)
        } catch (e: Exception) {
            logger.error("Error al obtener cuentas auxiliares por id de compañia", e)
            throw PostgresException("Error al obtener cuentas auxiliares por id de compañia", e.message.toString())
        }
    }

    fun updateAuxiliaryAccount(auxiliaryId: Int, name: String, code: String){
        try {
            logger.info("Actualizando cuenta auxiliar")
            auxiliaryAccountDao.update(auxiliaryId,name, code)
        } catch (e: Exception) {
            logger.error("Error al actualizar cuenta auxiliar", e)
            throw PostgresException("Error al actualizar cuenta auxiliar", e.message.toString())
        }
    }

    fun existCodeName(code: String, companyId: Int): Boolean{
        try {
            logger.info("Verificando si existe el codigo de cuenta auxiliar")
            return auxiliaryAccountDao.existsByCode(code, companyId)
        } catch (e: Exception) {
            logger.error("Error al verificar si existe el codigo de cuenta auxiliar", e)
            throw PostgresException("Error al verificar si existe el codigo de cuenta auxiliar", e.message.toString())
        }
    }
}