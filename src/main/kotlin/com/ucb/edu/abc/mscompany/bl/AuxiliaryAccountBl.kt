package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.AuxiliaryAccountDao
import com.ucb.edu.abc.mscompany.entity.AuxiliaryAccountEntity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class AuxiliaryAccountBl @Autowired constructor(
    private val auxiliaryAccountDao: AuxiliaryAccountDao
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getAuxiliariesAccountByCompanyId(companyId: Int): List<AuxiliaryAccountEntity>{
        try {
            logger.info("Obteniendo cuentas auxiliares por id de compañia")
            return auxiliaryAccountDao.getAuxiliaryAccountByCompanyId(companyId)
        } catch (e: Exception) {
            logger.error("Error al obtener cuentas auxiliares por id de compañia", e)
            throw e
        }
    }
}