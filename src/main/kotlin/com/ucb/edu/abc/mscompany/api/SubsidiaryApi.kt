package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.CompanyBl
import com.ucb.edu.abc.mscompany.bl.SubsidiaryBl
import com.ucb.edu.abc.mscompany.dto.request.EnterpriseDto
import com.ucb.edu.abc.mscompany.dto.request.SubsidiaryConfigDto
import com.ucb.edu.abc.mscompany.dto.request.DeleteAreasDto
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Private

@RestController
@RequestMapping("/config/enterprise")
class SubsidiaryApi @Autowired constructor(

        private val subsidiaryBl: SubsidiaryBl
){
    @GetMapping("/{companyId}/subsidiary")
    fun getSubsidiariesAndAreas(@PathVariable companyId: Int): ResponseDto<SubsidiaryConfigDto> {
        val subsidiaryConfigDto = subsidiaryBl.getSubsidiaryandAreas(companyId)
        return ResponseDto(subsidiaryConfigDto, "Request exitoso", true, "", )
    }

    @PostMapping("/{companyId}/subsidiary")
    fun deleteSubsidiary(@PathVariable companyId: Int, @RequestBody deleteDto: DeleteAreasDto): ResponseDto<SubsidiaryConfigDto> {
        subsidiaryBl.deleteSubsidiary(deleteDto)
        val subsidiaryConfig= getSubsidiariesAndAreas(companyId)
        return ResponseDto(subsidiaryConfig.data, "Request exitoso", true, "", )
    }


}
