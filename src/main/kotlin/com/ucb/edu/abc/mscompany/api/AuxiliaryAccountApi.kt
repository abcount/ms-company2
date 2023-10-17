package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.AuxiliaryAccountBl
import com.ucb.edu.abc.mscompany.dto.request.AuxiliaryDataDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import com.ucb.edu.abc.mscompany.entity.AuxiliaryAccountEntity
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
@RequestMapping("/auxiliaryAccount")
class AuxiliaryAccountApi @Autowired constructor(
        private val auxiliaryAccountBl: AuxiliaryAccountBl,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/{companyId}")
    fun getAllAuxiliaryAccountByCompanyId(@PathVariable companyId: Int): ResponseEntity<ResponseDto<List<AuxiliaryDataDto>>>{
        logger.info("Obteniendo cuentas auxiliares por id de compañia")
        val auxiliaryAccountList = auxiliaryAccountBl.getAuxiliariesAccountByCompanyId(companyId).map{
            AuxiliaryDataDto(
                    it.auxiliaryAccountId,
                    it.nameDescription,
                    it.codeAccount
            )
        }
        return ResponseEntity.ok(
            ResponseDto(auxiliaryAccountList, "Datos obtenidos con exito", true, "" ))
    }

    @PostMapping("/{companyId}")
    fun createAuxiliaryAccount(@RequestBody auxiliaryDataDto: AuxiliaryDataDto, @PathVariable companyId: Int): ResponseEntity<ResponseDto<List<AuxiliaryDataDto>>>{
        logger.info("Creando cuenta auxiliar")
         auxiliaryAccountBl.createAuxiliaryAccount(
                AuxiliaryAccountEntity(
                    0,
                    auxiliaryDataDto.auxiliaryCode,
                    auxiliaryDataDto.auxiliaryName,
                    companyId
                ))
        val listAuxliar = auxiliaryAccountBl.getAuxiliariesAccountByCompanyId(companyId).map {
            AuxiliaryDataDto(
                    it.auxiliaryAccountId,
                    it.nameDescription,
                    it.codeAccount
            )
        }
        return ResponseEntity.ok(
            ResponseDto(listAuxliar, "Cuenta auxiliar creada con exito", true, "" ))
    }

    @PutMapping("/{companyId}")
    fun updateAuxiliaryAccount(@PathVariable companyId: Int, @RequestBody auxiliaryDataDto: AuxiliaryDataDto): ResponseEntity<ResponseDto<List<AuxiliaryDataDto>>>{
        logger.info("Actualizando cuenta auxiliar")
        auxiliaryAccountBl.updateAuxiliaryAccount(
                auxiliaryDataDto.auxiliaryId!!,
                auxiliaryDataDto.auxiliaryName,
                auxiliaryDataDto.auxiliaryCode)

        val listAuxiliaries = auxiliaryAccountBl.getAuxiliariesAccountByCompanyId(companyId).map {
            AuxiliaryDataDto(
                    it.auxiliaryAccountId,
                    it.nameDescription,
                    it.codeAccount
            )
        }
        return ResponseEntity.ok(
            ResponseDto(listAuxiliaries, "Cuenta auxiliar actualizada con exito", true, "" ))
    }

}