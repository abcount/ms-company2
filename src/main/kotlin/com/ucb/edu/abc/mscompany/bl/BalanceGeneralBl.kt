    package com.ucb.edu.abc.mscompany.bl

    import com.ucb.edu.abc.mscompany.dao.*
    import com.ucb.edu.abc.mscompany.dto.request.BalanceGeneralRequestDto
    import com.ucb.edu.abc.mscompany.dto.request.JournalRequestDto
    import com.ucb.edu.abc.mscompany.dto.response.*
    import org.slf4j.Logger
    import org.slf4j.LoggerFactory
    import org.springframework.beans.factory.annotation.Autowired
    import org.springframework.stereotype.Service
    import java.math.BigDecimal
    import java.text.NumberFormat
    import java.text.SimpleDateFormat
    import java.util.*

    @Service
    class BalanceGeneralBl @Autowired constructor(
            private val subsidiaryDao: SubsidiaryDao,
            private val companyDao: CompanyDao,
            private val areaDao: AreaDao,
            private val exchangeRateDao: ExchangeRateDao,
            private val exchangeMoneyBl: ExchangeMoneyBl,
            private val accountDao: AccountDao,
            private val areaSubsidiaryDao: AreaSubsidiaryDao,
    ) {

        private val logger: Logger = LoggerFactory.getLogger(CompanyBl::class.java)
        fun getBalanceGeneral(companyId: Int, balanceGeneralRequestDto: BalanceGeneralRequestDto): BalanceGeneralResponseDto {
            logger.info("Obteniendo balance general de la empresa: $companyId")
            val companyEntity = companyDao.getCompanyById(companyId)

            val exchangeMoney = exchangeMoneyBl.getExchangeMoneyByCompanyIdAndISO(companyId, balanceGeneralRequestDto.currencies)

            val subsidiaryBalanceDtoList= mutableListOf<SubsidiaryBalance>()
            for (subsidiaryId in balanceGeneralRequestDto.subsidiaries) {
                val subsidiaryEntity = subsidiaryDao.getSubsidiaryById(subsidiaryId)
                var areaBalanceDtoList = mutableListOf<AreaBalance>()
                for (areaId in balanceGeneralRequestDto.areas) {

                    val areaEntity = areaDao.getAreaById(areaId)
                    val areaSubsidiaryId= areaSubsidiaryDao.findAreaSubsidiaryId(subsidiaryEntity.subsidiaryId, areaEntity.areaId)

                    var listAccountBalanced= getAccountBalance(companyId,balanceGeneralRequestDto.to, areaSubsidiaryId, exchangeMoney.abbreviationName)



                    //BALANCE GENERAL
                    val activeTotal = listAccountBalanced.filter { it.accountCode.startsWith("1") }.sumOf { it.amount }
                    val passiveTotal = listAccountBalanced.filter { it.accountCode.startsWith("2") }.sumOf { it.amount }
                    val patrimonyTotal = listAccountBalanced.filter { it.accountCode.startsWith("3") }.sumOf { it.amount }
                    val ingresosTotal = listAccountBalanced.filter { it.accountCode.startsWith("4") }.sumOf { it.amount }
                    val gastosTotal = listAccountBalanced.filter { it.accountCode.startsWith("5") }.sumOf { it.amount }

                    val totalResult = ingresosTotal - gastosTotal

                    val totalPassiveCapital = passiveTotal + patrimonyTotal

                    listAccountBalanced = listAccountBalanced.filterNot {
                        it.accountCode.startsWith("4") || it.accountCode.startsWith("5")
                    }.toMutableList()

                    areaBalanceDtoList.add(AreaBalance(
                            subsidiaryId,
                            areaEntity.areaId,
                            areaEntity.areaName,
                            listAccountBalanced,
                            activeTotal,
                            totalPassiveCapital,
                            totalResult,
                            activeTotal,
                            totalResult+totalPassiveCapital
                    ))
                }

                subsidiaryBalanceDtoList.add(SubsidiaryBalance(subsidiaryEntity.subsidiaryId, subsidiaryEntity.subsidiaryName, areaBalanceDtoList))
            }

            return BalanceGeneralResponseDto(companyEntity.companyName, balanceGeneralRequestDto.to, exchangeMoney.moneyName, balanceGeneralRequestDto.responsible, subsidiaryBalanceDtoList)
        }

        fun getAccountBalance(companyId: Int, to: Date, areaSubsidiaryId: Int?, exchangeRateId: String ): List<AccountBalance>{
            var accountBalanceDtoList = mutableListOf<AccountBalance>()
            var activeAccountId= accountDao.getAccountIdBYCode("1", companyId)
            var passiveAccountId= accountDao.getAccountIdBYCode("2", companyId)
            var patrimonyAccountId= accountDao.getAccountIdBYCode("3", companyId)
            var ingresos = accountDao.getAccountIdBYCode("4", companyId)
            var gastos = accountDao.getAccountIdBYCode("5", companyId)



            val activeAccountTree = buildAccountTree(activeAccountId, companyId, to, areaSubsidiaryId, exchangeRateId)
            val passiveAccountTree = buildAccountTree(passiveAccountId, companyId,to, areaSubsidiaryId, exchangeRateId)
            val patrimonyAccountTree = buildAccountTree(patrimonyAccountId, companyId,to, areaSubsidiaryId, exchangeRateId)
            val ingresosAccountTree = buildAccountTree(ingresos, companyId,to, areaSubsidiaryId, exchangeRateId)
            val gastosAccountTree = buildAccountTree(gastos, companyId,to, areaSubsidiaryId, exchangeRateId)


            accountBalanceDtoList.add(activeAccountTree)
            accountBalanceDtoList.add(passiveAccountTree)
            accountBalanceDtoList.add(patrimonyAccountTree)
            accountBalanceDtoList.add(ingresosAccountTree)
            accountBalanceDtoList.add(gastosAccountTree)

            return accountBalanceDtoList


        }

        private fun buildAccountTree(rootAccountId: Int, companyId: Int,to: Date, areaSubsidiaryId: Int?, exchangeId: String): AccountBalance {
            // Obtenemos todas las cuentas de la compañía
            val allAccounts = accountDao.getAccountPlanByCompanyId(companyId).associateBy { it.accountId }

            // Función recursiva para construir el árbol y sumar montos
            fun buildTree(accountId: Int): AccountBalance {
                val currentAccount = allAccounts[accountId] ?: throw IllegalStateException("Account not found for ID: $accountId")
                val children = allAccounts.values
                        .filter { it.accountAccountId == accountId }
                        .map { buildTree(it.accountId) }

                val amount = if (children.isEmpty()) {
                    //accountDao.getBalanceByAccount(accountId,to, areaSubsidiaryId, exchangeId) ?: BigDecimal.ZERO
                    /***/
                    if (currentAccount.codeAccount.startsWith("1") || currentAccount.codeAccount.startsWith("5")){
                        accountDao.getBalanceByAccount(accountId,to, areaSubsidiaryId, exchangeId) ?: BigDecimal.ZERO

                    } else {
                        accountDao.getBalancePassive(accountId,to, areaSubsidiaryId, exchangeId) ?: BigDecimal.ZERO
                    }
                    /***/
                } else {
                    children.fold(BigDecimal.ZERO) { total, child -> total.add(child.amount) }
                }

                return AccountBalance(currentAccount.codeAccount, currentAccount.nameAccount, amount, children)
            }

            return buildTree(rootAccountId)
        }


        fun getBalanceGeneralPDF(companyId: Int, balanceGeneralRequestDto: BalanceGeneralRequestDto): BalanceGeneralResponseDtoPDF {
            logger.info("Obteniendo balance general de la empresa: $companyId")
            val companyEntity = companyDao.getCompanyById(companyId)

            val exchangeMoney = exchangeMoneyBl.getExchangeMoneyByCompanyIdAndISO(companyId, balanceGeneralRequestDto.currencies)

            val subsidiaryBalanceDtoList= mutableListOf<SubsidiaryBalancePDF>()
            for (subsidiaryId in balanceGeneralRequestDto.subsidiaries) {
                val subsidiaryEntity = subsidiaryDao.getSubsidiaryById(subsidiaryId)
                var areaBalanceDtoList = mutableListOf<AreaBalancePDF>()
                for (areaId in balanceGeneralRequestDto.areas) {

                    val areaEntity = areaDao.getAreaById(areaId)
                    val areaSubsidiaryId= areaSubsidiaryDao.findAreaSubsidiaryId(subsidiaryEntity.subsidiaryId, areaEntity.areaId)

                    var listAccountBalanced= getAccountBalance(companyId,balanceGeneralRequestDto.to, areaSubsidiaryId, exchangeMoney.abbreviationName)



                    //BALANCE GENERAL
                    val activeTotal = listAccountBalanced.filter { it.accountCode.startsWith("1") }.sumOf { it.amount }
                    val passiveTotal = listAccountBalanced.filter { it.accountCode.startsWith("2") }.sumOf { it.amount }
                    val patrimonyTotal = listAccountBalanced.filter { it.accountCode.startsWith("3") }.sumOf { it.amount }
                    val ingresosTotal = listAccountBalanced.filter { it.accountCode.startsWith("4") }.sumOf { it.amount }
                    val gastosTotal = listAccountBalanced.filter { it.accountCode.startsWith("5") }.sumOf { it.amount }

                    val totalResult = ingresosTotal - gastosTotal

                    val totalPassiveCapital = passiveTotal + patrimonyTotal

                    listAccountBalanced = listAccountBalanced.filterNot {
                        it.accountCode.startsWith("4") || it.accountCode.startsWith("5")
                    }.toMutableList()

                    val listAccount = listAccountBalanced.map{
                        convertToAccountBalancePDF(it)
                    }

                    areaBalanceDtoList.add(AreaBalancePDF(
                            subsidiaryId,
                            areaEntity.areaId,
                            areaEntity.areaName,
                            listAccount,
                            getNumber(activeTotal),
                            getNumber(totalPassiveCapital),
                            getNumber(totalResult),
                            getNumber(activeTotal),
                            getNumber(totalResult+totalPassiveCapital)
                    ))
                }

                subsidiaryBalanceDtoList.add(SubsidiaryBalancePDF(subsidiaryEntity.subsidiaryId, subsidiaryEntity.subsidiaryName, areaBalanceDtoList))
            }

            return BalanceGeneralResponseDtoPDF(companyEntity.companyName, convertDateToString(balanceGeneralRequestDto.to), exchangeMoney.moneyName, balanceGeneralRequestDto.responsible, subsidiaryBalanceDtoList)
        }



        fun convertToAccountBalancePDF(accountBalance: AccountBalance): AccountBalancePDF {
            return AccountBalancePDF(accountBalance.accountCode, accountBalance.accountName, getNumber(accountBalance.amount), accountBalance.children.map { convertToAccountBalancePDF(it) })
        }

        fun convertDateToString(date: Date): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            return formatter.format(date)
        }

        fun getNumber(number: BigDecimal): String{
            val format = NumberFormat.getNumberInstance(Locale("es", "ES"))
            format.minimumFractionDigits = 2
            format.maximumFractionDigits = 2
            return format.format(number)
        }


    }