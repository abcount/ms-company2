package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.config.FormatDataClass
import com.ucb.edu.abc.mscompany.dao.*
import com.ucb.edu.abc.mscompany.dto.request.EstadoResultadosRequestDto
import com.ucb.edu.abc.mscompany.dto.response.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

@Service
class EstadoResultadosBl @Autowired constructor(
        private val subsidiaryDao: SubsidiaryDao,
        private val companyDao: CompanyDao,
        private val areaDao: AreaDao,
        private val exchangeRateDao: ExchangeRateDao,
        private val accountDao: AccountDao,
        private val areaSubsidiaryDao: AreaSubsidiaryDao,
        private val exchangeMoneyBl: ExchangeMoneyBl,
        private val companyBl: CompanyBl,
        private val formatDataClass: FormatDataClass,
        private val accessPersonBl: AccessPersonBl
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    fun getEstadoResultados(companyId: Int, estadoResultadosRequestDto: EstadoResultadosRequestDto): EstadoResultadosResponseDto {
        logger.info("Obteniendo el estado de reultados de la empresa: $companyId")
        val companyEntity = companyDao.getCompanyById(companyId)

        val exchangeMoney = exchangeMoneyBl.getExchangeMoneyByCompanyIdAndISO(companyId, estadoResultadosRequestDto.currencies)

        val subsidiaryStateDtoList= mutableListOf<SubsidiaryState>()
        for (subsidiaryId in estadoResultadosRequestDto.subsidiaries) {
            val subsidiaryEntity = subsidiaryDao.getSubsidiaryById(subsidiaryId)
            var areaStateDtoList = mutableListOf<AreaState>()
            for (areaId in estadoResultadosRequestDto.areas) {

                val areaEntity = areaDao.getAreaById(areaId)
                val areaSubsidiaryId= areaSubsidiaryDao.findAreaSubsidiaryId(subsidiaryEntity.subsidiaryId, areaEntity.areaId)

                var listAccountState= getAccountBalance(
                    companyId,formatDataClass.stringToDateAtBeginOfDay(estadoResultadosRequestDto.from),
                    formatDataClass.stringToDateAtEndOfDay(estadoResultadosRequestDto.to), areaSubsidiaryId, exchangeMoney.abbreviationName)


                val ingresosTotal = listAccountState.filter { it.accountCode.startsWith("4") }.sumOf { it.amount }
                val gastosTotal = listAccountState.filter { it.accountCode.startsWith("5") }.sumOf { it.amount }

                val totalResult = ingresosTotal - gastosTotal


                areaStateDtoList.add(AreaState(
                        subsidiaryId,
                        areaEntity.areaId,
                        areaEntity.areaName,
                        listAccountState,
                        totalResult

                ))
            }

            subsidiaryStateDtoList.add(SubsidiaryState(subsidiaryEntity.subsidiaryId, subsidiaryEntity.subsidiaryName, areaStateDtoList))
        }

        return EstadoResultadosResponseDto(companyEntity.companyName, formatDataClass.stringToDateAtEndOfDay( estadoResultadosRequestDto.to),
            exchangeMoney.moneyName, estadoResultadosRequestDto.responsible ,subsidiaryStateDtoList)
    }

    fun getAccountBalance(companyId: Int, from:Date ,to: Date, areaSubsidiaryId: Int?, exchangeRateId: String ): List<AccountState>{
        var accountStateDtoList = mutableListOf<AccountState>()
        var ingresos = accountDao.getAccountIdBYCode("4", companyId)
        var gastos = accountDao.getAccountIdBYCode("5", companyId)


        val ingresosAccountTree = buildAccountTree(ingresos, companyId,from,to, areaSubsidiaryId, exchangeRateId)
        val gastosAccountTree = buildAccountTree(gastos, companyId,from,to, areaSubsidiaryId, exchangeRateId)

        accountStateDtoList.add(ingresosAccountTree)
        accountStateDtoList.add(gastosAccountTree)

        return accountStateDtoList.filter { it.amount != BigDecimal.ZERO }

    }

    private fun buildAccountTree(rootAccountId: Int, companyId: Int,from:Date,to: Date, areaSubsidiaryId: Int?, exchangeId: String): AccountState {
        // Obtenemos todas las cuentas de la compañía
        val allAccounts = accountDao.getAccountPlanByCompanyId(companyId).associateBy { it.accountId }

        // Función recursiva para construir el árbol y sumar montos
        fun buildTree(accountId: Int): AccountState {
            val currentAccount = allAccounts[accountId] ?: throw IllegalStateException("Account not found for ID: $accountId")
            val children = allAccounts.values
                    .filter { it.accountAccountId == accountId }
                    .map { buildTree(it.accountId) }
                    .filter{ it.amount != BigDecimal.ZERO }

            val amount = if (children.isEmpty()) {
                //accountDao.getBalanceByAccount(accountId,to, areaSubsidiaryId, exchangeId) ?: BigDecimal.ZERO
                /***/
                if (currentAccount.codeAccount.startsWith("1") || currentAccount.codeAccount.startsWith("5")){
                    accountDao.getStateByAccount(accountId,from,to, areaSubsidiaryId, exchangeId) ?: BigDecimal.ZERO

                } else {
                    accountDao.getStatePassive(accountId,from,to, areaSubsidiaryId, exchangeId) ?: BigDecimal.ZERO
                }
                /***/
            } else {
                children.fold(BigDecimal.ZERO) { total, child -> total.add(child.amount) }
            }

            return AccountState(currentAccount.codeAccount, currentAccount.nameAccount, amount, children)
        }

        return buildTree(rootAccountId)
    }


    suspend fun getEstadoResultadosPDF(companyId: Int, estadoResultadosRequestDto: EstadoResultadosRequestDto, header: Map<String,String>): EstadoResultadosResponseDtoPDF {
        logger.info("Obteniendo el estado de reultados de la empresa: $companyId")
        val companyEntity = companyDao.getCompanyById(companyId)
        val url = companyBl.getUrlImageByCompanyId(companyId)
        val exchangeMoney = exchangeMoneyBl.getExchangeMoneyByCompanyIdAndISO(companyId, estadoResultadosRequestDto.currencies)

        val subsidiaryStateDtoList= mutableListOf<SubsidiaryStatePDF>()
        for (subsidiaryId in estadoResultadosRequestDto.subsidiaries) {
            val subsidiaryEntity = subsidiaryDao.getSubsidiaryById(subsidiaryId)
            var areaStateDtoList = mutableListOf<AreaStatePDF>()
            for (areaId in estadoResultadosRequestDto.areas) {

                val areaEntity = areaDao.getAreaById(areaId)
                val areaSubsidiaryId= areaSubsidiaryDao.findAreaSubsidiaryId(subsidiaryEntity.subsidiaryId, areaEntity.areaId)

                var listAccountState= getAccountBalance(
                    companyId,formatDataClass.stringToDateAtBeginOfDay(estadoResultadosRequestDto.from),
                    formatDataClass.stringToDateAtEndOfDay(estadoResultadosRequestDto.from), areaSubsidiaryId, exchangeMoney.abbreviationName)


                val ingresosTotal = listAccountState.filter { it.accountCode.startsWith("4") }.sumOf { it.amount }
                val gastosTotal = listAccountState.filter { it.accountCode.startsWith("5") }.sumOf { it.amount }

                val totalResult = ingresosTotal - gastosTotal


                val listAccount = listAccountState.map{
                    convertAccountStateToPdf(it)
                }

                areaStateDtoList.add(AreaStatePDF(
                        subsidiaryId,
                        areaEntity.areaId,
                        areaEntity.areaName,
                        listAccount,
                        formatDataClass.getNumber(totalResult)

                ))
            }

            subsidiaryStateDtoList.add(SubsidiaryStatePDF(subsidiaryEntity.subsidiaryId, subsidiaryEntity.subsidiaryName, areaStateDtoList))
        }

        val date = LocalDateTime.now()
        val userEntity = accessPersonBl.getAccessPersonInformationByToken(header["authorization"]!!.substring(7))
        val userName = userEntity!!.firstName + " " + userEntity!!.lastName

        return EstadoResultadosResponseDtoPDF(
                companyEntity.companyName,
                url,
                formatDataClass.getDateFromLocalDateTime(date),
                formatDataClass.getHourFromLocalDateTime(date),
                userName,
                formatDataClass.changeFormatStringDate(estadoResultadosRequestDto.from),
                exchangeMoney.moneyName,
                estadoResultadosRequestDto.responsible,
                subsidiaryStateDtoList)
    }

    fun convertAccountStateToPdf(account: AccountState): AccountStatePDF{
        val filteredChildren = account.children.filter { it.amount != BigDecimal.ZERO }

        return AccountStatePDF(
                account.accountCode,
                account.accountName,
                formatDataClass.getNumber(account.amount),
                filteredChildren.map { convertAccountStateToPdf(it) }

        )
    }







}