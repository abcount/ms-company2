package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.AreaDao
import com.ucb.edu.abc.mscompany.dao.AreaSubsidiaryDao
import com.ucb.edu.abc.mscompany.dao.SubsidiaryDao
import com.ucb.edu.abc.mscompany.dto.response.AreaDtoRes
import com.ucb.edu.abc.mscompany.dto.response.SubsidiaryDtoRes

import com.ucb.edu.abc.mscompany.entity.AreaSubsidiaryEntity
import com.ucb.edu.abc.mscompany.entity.pojos.SubsidiaryAndAreaPojo
import org.apache.ibatis.exceptions.PersistenceException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AreaSubsidiaryBl @Autowired constructor(
        private val areaSubsidiaryDao: AreaSubsidiaryDao,
    private val areaDao: AreaDao,
    private val subsidiaryDao: SubsidiaryDao,
    private val roleBl: RoleBl
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun create(areaSubsidiaryEntity: AreaSubsidiaryEntity): Int {
        try {
            logger.info("Creando area-sucursal")
            areaSubsidiaryDao.create(areaSubsidiaryEntity)
            return areaSubsidiaryEntity.areaSubsidiaryId
        } catch (e: PersistenceException) {
            logger.error("Error al crear area-sucursal", e)
            return 0
        }
    }


    fun factoryAreaSubsidiary(areaId:Int, subsidiaryId: Int, diccCategory: String): AreaSubsidiaryEntity{
        val areaSubsidiaryEntity = AreaSubsidiaryEntity()
        areaSubsidiaryEntity.areaId = areaId
        areaSubsidiaryEntity.subsidiaryId = subsidiaryId
        areaSubsidiaryEntity.diccCategory = diccCategory
        return areaSubsidiaryEntity
    }

    fun getAreasSubsidiaryByCompany(companyId: Int): List<SubsidiaryAndAreaPojo> {
        return areaSubsidiaryDao.findByCompanyId(companyId);
    }

    fun getAreaSubsidiaryAndRolesByCompany(companyId: Int): MutableMap<String, Any> {

        val areaSub = getAreasSubsidiaryByCompany(companyId);

        val mapOfValues: MutableMap<Int, SubsidiaryDtoRes> = mutableMapOf()

        areaSub.forEach { item ->
            val id = item.subsidiaryId;
            if (mapOfValues[id] == null) {
                mapOfValues[id] = SubsidiaryDtoRes(
                    subsidiaryId = item.subsidiaryId,
                    subsidiaryName = item.subsidiaryName,
                    areas = mutableListOf()
                )
            }
            val area: AreaDtoRes = AreaDtoRes(
                areaId = item.areaId,
                areaName = item.areaName,
                areaSubsidiaryId = item.areaSubsidiaryId,
                status = false
            )
            mapOfValues[id]?.areas?.add(area)
        }

        //val returned = mapOfValues.map { (key, value) ->
        //    value
        //}

        return mutableMapOf(
            "areasAndSubs" to mapOfValues.values.toList(),
            "roles" to roleBl.getAllRolesSimpleDto()
        )
    }

    fun getAreaSubIdByCompany( areaId: Int, subsidiaryId: Int): Int? {
        return areaSubsidiaryDao.findAreaSubsidiaryId(subsidiaryId, areaId)
    }



}