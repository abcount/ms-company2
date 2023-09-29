package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dto.request.CreateCompanyDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile


@Service
class ConfigCompany @Autowired constructor(
        private val companyBl: CompanyBl,
        private val areaBl: AreaBl,
        private val subsidiaryBl: SubsidiaryBl,
        private val areaSubsidiaryBl: AreaSubsidiaryBl,
        private val exchangeMoneyBl: ExchangeMoneyBl,
        private val accountBl: AccountBl,
){

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createCompany(createCompanyDto: CreateCompanyDto, tokenAuth: String, image: MultipartFile): Int {
        //Crear la compañia y devolver los datos de compañia
        val companyEntity = companyBl.factoryCompany(createCompanyDto.enterprise, "MM-dd-yyyy", image)
        val companyId = companyBl.create(companyEntity)

        //Crear Sucursales y areas
        var listOfAreasSubsidiary = mutableListOf<Int>()
        var areasCreated: MutableMap<String, Int> = mutableMapOf()
        createCompanyDto.enterprise.subsidiaries.forEach() {
            if (it != null) {
                val subsidiaryEntity = subsidiaryBl.factorySubsidiary(it, companyId)
                subsidiaryBl.create(subsidiaryEntity)
                it.areas.forEach(){ area ->
                    if (area != null && !areasCreated.containsKey(area)) {
                        val areaEntity = areaBl.factoryArea(area, companyId, null)
                        val areaId = areaBl.create(areaEntity)
                        areasCreated[area] = areaId
                    }
                    val areaIdCreated = areasCreated[area]
                    if (areaIdCreated != null) {
                        val areaSubsidiaryEntity = areaSubsidiaryBl.factoryAreaSubsidiary(areaIdCreated, subsidiaryEntity.subsidiaryId, "")
                        val areaSubsidiaryId = areaSubsidiaryBl.create(areaSubsidiaryEntity)
                        listOfAreasSubsidiary.add(areaSubsidiaryId)
                    }
                }
            }
        }

        //Crear lista de cambios de moneda
        createCompanyDto.currencyConfig.currencyList.forEachIndexed() { index, element ->
            if(element != null){
                val currencyEntity = exchangeMoneyBl.factoryExchangeMoney(companyId, element.moneyName, element.abbreviationName, false)
                if(index == createCompanyDto.currencyConfig.principalCurrency){
                    currencyEntity.isPrincipal = true
                }
                exchangeMoneyBl.create(currencyEntity)
            }
        }

        //Crear plan de cuentas
        accountBl.createAccountPlan(createCompanyDto.accountablePlan, companyId)
        return companyId
    }

}