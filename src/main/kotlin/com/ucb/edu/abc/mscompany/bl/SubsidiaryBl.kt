package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.SubsidiaryDao
import com.ucb.edu.abc.mscompany.dao.AreaDao
import com.ucb.edu.abc.mscompany.dao.AreaSubsidiaryDao
import com.ucb.edu.abc.mscompany.dto.request.*

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
    private val areaDao: AreaDao,
    private val areaSubsidiaryDao: AreaSubsidiaryDao,
    private var areaBl: AreaBl,
    private var areaSubsidiaryBl: AreaSubsidiaryBl,
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

    fun getByCompanyId(companyId: Int): List<SubsidiaryEntity>{
        try {
            logger.info("Obteniendo sucursales por id de compañia")
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




    fun getSubsidiaryandAreas(companyId: Int):SubsidiaryConfigDto{
        try {
            var areas = areaDao.getAreasByCompanyId(companyId);
            var subsidiaries= subsidiaryDao.getSubsidiariesByCompanyId(companyId);
            //var areasList = areas.map { it.areaId, it.areaName ?: ""}
            var areasList = areas.map {
                AreaDto( it.areaId ,it.areaName ?: "")
            }
            var subsidiariesList = subsidiaries.map {
                AddSubsidiaryDto( it.subsidiaryId ,it.address ?: "", it.subsidiaryName ?: "")
            }
            return SubsidiaryConfigDto(subsidiariesList, areasList)
        } catch (e: Exception) {
            throw PostgresException("Ocurrio un error al obtener las sucursales de la empresa con id: ", e.message.toString())
        }
    }


    fun deleteSubsidiary(deleteAreasDto: DeleteAreasDto ){
        try {
            val areasToDelete = deleteAreasDto.areas
            /*for (areaId in areasToDelete) {
                areaDao.updateStatus(areaId)
            }*/
            deleteAreasDto.areas.forEach { areaId ->
                areaDao.updateStatus(areaId)}
            //falta el de subisiares

        }catch (e: Exception) {
            throw PostgresException("Ocurrio un error al obtener las sucursales de la empresa con id: ", e.message.toString())
        }
    }

    fun newSubsidiaries(companyId: Int, newSubsidiariesDto: SubsidiaryConfigDto) {
        try {
            val allSubsidiaries = subsidiaryDao.getSubsidiariesByCompanyId(companyId).map { it.subsidiaryId }.toMutableList()
            val allAreas = areaDao.getAreasByCompanyId(companyId).map { it.areaId }.toMutableList()

            // Crear nueva sucursal
            newSubsidiariesDto.subsidiaries.forEach { subsidiary ->
                val id = create(factorynewSubsidiaries(companyId, subsidiary))
                allSubsidiaries.add(id)
            }

            // crear nueva area
            newSubsidiariesDto.areas.forEach { area ->
                val id = areaBl.create(areaBl.factoryArea(area.areaName, companyId, null))
                allAreas.add(id)
            }

            // Para cada sucursal asegurarse de que tenga todas las áreas.
            allSubsidiaries.forEach { subsidiaryId ->
                allAreas.forEach { areaId ->
                    // Verificar si ya existe en la BD
                    val existing = areaSubsidiaryDao.findByAreaAndSubsidiary(areaId, subsidiaryId)
                    if (existing == null) {
                        areaSubsidiaryBl.create(areaSubsidiaryBl.factoryAreaSubsidiary(areaId, subsidiaryId, "enable"))
                    }
                }
            }

        } catch (e: Exception) {
            throw PostgresException("Ocurrio un error al obtener las sucursales de la empresa con id: $companyId", e.message.toString())
        }
    }




    // esta funcion se debe a que el nuevo json de agregar nuevas sucursales es distinto, areas y suscursales, vienen en el mismo json
    // pero distintos arreglos xd
    fun factorynewSubsidiaries(companyId: Int, addSubsidiaryDto: AddSubsidiaryDto): SubsidiaryEntity{

        try {
            val subsidiaryEntity = SubsidiaryEntity()
            subsidiaryEntity.companyId = companyId
            subsidiaryEntity.subsidiaryName = addSubsidiaryDto.subsidiaryName
            subsidiaryEntity.address = addSubsidiaryDto.address
            return subsidiaryEntity
        }catch (e: Exception) {
            throw PostgresException("Ocurrio un error al obtener las sucursales de la empresa con id: ", e.message.toString())
        }
    }



}