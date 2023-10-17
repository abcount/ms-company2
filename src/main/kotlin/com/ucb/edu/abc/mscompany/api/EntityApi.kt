package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.EntityBl
import com.ucb.edu.abc.mscompany.dto.request.EntityDataDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import com.ucb.edu.abc.mscompany.entity.EntityEntity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/entity")
class EntityApi @Autowired constructor(
    private val entityBl: EntityBl
){

    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/{companyId}")
    fun getAllEntitiesByCompanyId(@PathVariable companyId: Int): ResponseEntity<ResponseDto<List<EntityDataDto>>>{
        logger.info("Obteniendo entidades por id de compañia")
        val entityList = entityBl.getAllEntitiesByCompanyId(companyId).map{
            EntityDataDto(
                it.entityId,
                it.entityName,
                it.nit,
                it.socialReason,
                it.foreign
            )
        }
        return ResponseEntity.ok(
            ResponseDto(entityList, "Datos obtenidos con exito", true, "" ))
    }

    @PostMapping("/{companyId}")
    fun createEntity(@PathVariable companyId: Int,@RequestBody entityDataDto: EntityDataDto): ResponseEntity<ResponseDto<List<EntityDataDto>>>{
        logger.info("Creando entidad")
        entityBl.createEntity(
                EntityEntity(
                        0,
                        companyId,
                        entityDataDto.entityName,
                        entityDataDto.entityNit,
                        entityDataDto.entitySocialReason,
                        entityDataDto.foreign
                )
        )
        val listEntity = entityBl.getAllEntitiesByCompanyId(companyId).map {
            EntityDataDto(
                    it.entityId,
                    it.entityName,
                    it.nit,
                    it.socialReason,
                    it.foreign
            )
        }
        return ResponseEntity.ok(
            ResponseDto(listEntity, "Entidad creada con exito", true, "" ))
    }

    @PutMapping("/{companyId}")
    fun updateEntity(@PathVariable companyId: Int, @RequestBody entityDataDto: EntityDataDto): ResponseEntity<ResponseDto<List<EntityDataDto>>>{
        logger.info("Actualizando entidad")
        entityBl.updateEntity(
                EntityEntity(
                        entityDataDto.entityId!!,
                       0,
                        entityDataDto.entityName,
                        entityDataDto.entityNit,
                        entityDataDto.entitySocialReason,
                        entityDataDto.foreign
                )
        )
        val listEntity = entityBl.getAllEntitiesByCompanyId(companyId).map {
            EntityDataDto(
                    it.entityId,
                    it.entityName,
                    it.nit,
                    it.socialReason,
                    it.foreign
            )
        }
        return ResponseEntity.ok(
            ResponseDto(listEntity, "Entidad actualizada con exito", true, "" ))
    }
}