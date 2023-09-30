package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.SubsidiaryDao
import com.ucb.edu.abc.mscompany.dao.AreaDao

import com.ucb.edu.abc.mscompany.dto.request.SubsidiaryConfigDto
import com.ucb.edu.abc.mscompany.dto.request.SubsidiaryDto
import com.ucb.edu.abc.mscompany.dto.request.AddSubsidiaryDto
import com.ucb.edu.abc.mscompany.dto.request.AreaDto

import com.ucb.edu.abc.mscompany.entity.SubsidiaryEntity
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.apache.ibatis.exceptions.PersistenceException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.SQLException

@Service
class SubsidiaryBl @Autowired constructor(
    private val subsidiaryDao: SubsidiaryDao,
    private val AreaDao: AreaDao
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun create(subsidiaryEntity: SubsidiaryEntity): Int {
        try {
            logger.info("Creando sucursal")
            subsidiaryDao.create(subsidiaryEntity)
            return subsidiaryEntity.subsidiaryId
        } catch (e: Exception) {
            throw PostgresException("Ocurrio un error al crear la sucursal: ${subsidiaryEntity.toString()}", e.message.toString())
        }
    }

    fun get(subsidiaryId: Int): SubsidiaryEntity{
        try {
            logger.info("Obteniendo sucursal")
            return subsidiaryDao.getSubsidiaryById(subsidiaryId)
        } catch (e: Exception) {
            throw PostgresException("Ocurrio un error al obtener la sucursal con id: $subsidiaryId", e.message.toString())
        }
    }

    fun getAllByCompanyId(companyId: Int): List<SubsidiaryEntity>{
        try {
            logger.info("Obteniendo sucursales")
            return subsidiaryDao.getSubsidiariesByCompanyId(companyId)
        } catch (e: Exception) {
            throw PostgresException("Ocurrio un error al obtener las sucursales de la empresa con id: $companyId", e.message.toString())
        }
    }

    fun factorySubsidiary(subsidiaryDto: SubsidiaryDto, companyId: Int): SubsidiaryEntity{
        val subsidiaryEntity = SubsidiaryEntity()
        subsidiaryEntity.companyId = companyId
        subsidiaryEntity.subsidiaryName = subsidiaryDto.name
        subsidiaryEntity.address = subsidiaryDto.address
        logger.info("Se creara la sucursal: ${subsidiaryEntity.toString()}")
        return subsidiaryEntity
    }




    // NO olvidar el campo editable xd
    fun getSubsidiaryandAreas(companyId: Int):SubsidiaryConfigDto{
        try {
            var areas = AreaDao.getAreasByCompanyId(companyId);
            var subsidiaries= subsidiaryDao.getSubsidiariesByCompanyId(companyId);
            //var areasList = areas.map { it.areaId, it.areaName ?: ""}
            var areasList = areas.map {
                AreaDto( it.areaId ,it.areaName ?: "")
            }
            var subsidiariesList = subsidiaries.map {
                AddSubsidiaryDto( it.subsidiaryId ,it.subsidiaryName ?: "", it.address ?: "")
            }
            return SubsidiaryConfigDto(subsidiariesList, areasList)
        } catch (e: Exception) {
            throw PostgresException("Ocurrio un error al obtener las sucursales de la empresa con id: ", e.message.toString())
        }
    }

}