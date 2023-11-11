package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dto.request.CreateCompanyDto
import com.ucb.edu.abc.mscompany.enums.GroupCategory
import org.apache.tomcat.util.codec.binary.Base64
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
        private val exchangeBl: ExchangeBl,
    private val userBl: UserBl,
    private val permissionBl: PermissionBl,
        private val fileBl: FileBl
){

    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun createCompany(createCompanyDto: CreateCompanyDto, tokenAuth: String, image: MultipartFile): Int {
        //Crear la compañia y devolver los datos de compañia
        val companyEntity = companyBl.factoryCompany(createCompanyDto.enterprise, "MM-dd-yyyy", image)
        val uuid = fileBl.uploadImage(image)
        companyEntity.uuid = uuid
        val companyId = companyBl.create(companyEntity)

        // update Alan: convert file into Base 64 and save into new table 
        companyBl.saveThisFileWithId(companyId, image, "COMPANY-PROFILE-IMAGE")


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

        // create permissions for founder, high level permissions
        permissionBl.createPermissionsForCompany(tokenAuth, listOfAreasSubsidiary, companyId, GroupCategory.FOUNDER)

        //Crear lista de cambios de moneda
        //Se crea la moneda principal
        val bol = exchangeBl.getBoliviano();
        val exchangeBol = exchangeMoneyBl.factoryExchangeMoney(bol, companyId, true)
        exchangeMoneyBl.create(exchangeBol)

        //Se crea la lista recibida

        val listExchange = exchangeBl.getByArrayId(createCompanyDto.currencyConfig.currencyList)
        listExchange.forEach() {
            if(it.moneyName!=bol.moneyName){ //Verficar que no exista duplicado de moneda
                val exchangeMoneyEntity = exchangeMoneyBl.factoryExchangeMoney(it, companyId, false)
                exchangeMoneyBl.create(exchangeMoneyEntity)
            }
        }

        //Crear plan de cuentas
        accountBl.createAccountPlan(createCompanyDto.accountablePlan, companyId)
        return companyId
    }






}