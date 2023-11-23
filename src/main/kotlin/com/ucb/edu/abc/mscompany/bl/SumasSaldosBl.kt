package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.config.FormatDataClass
import com.ucb.edu.abc.mscompany.dao.*
import com.ucb.edu.abc.mscompany.dto.request.SumasSaldosRequestDto
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
class SumasSaldosBl @Autowired constructor(
    private val transactionDao: TransactionDao,
    private val companyDao: CompanyDao,
    private val subsidiaryDao: SubsidiaryDao,
    private val accountDao: AccountDao,
    private val exchangeRateDao: ExchangeRateDao,
    private val areaDao: AreaDao,
    private val areaSubsidiaryDao: AreaSubsidiaryDao,
    private val exchangeMoneyBl: ExchangeMoneyBl,
    private val companyBl: CompanyBl,
    private val formatDataClass: FormatDataClass,
    private val accessPersonBl: AccessPersonBl
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun getSumasSaldos(companyId: Int, sumasSaldosRequestDto: SumasSaldosRequestDto): SumasSaldosResponseDto {
        logger.info("Obteniendo sumas y saldos")
        val company = companyDao.getCompanyById(companyId)
        val currencyName = exchangeMoneyBl.getExchangeMoneyByCompanyIdAndISO(companyId, sumasSaldosRequestDto.currencies)
        val subsidiarySumas = mutableListOf<SubsidiarySumas>()
        val rootAccounts = accountDao.getRootAccounts(companyId)
        logger.info("Root accounts: $rootAccounts")
        val movementAccounts = accountDao.getMovementAccounts(companyId)
        logger.info("Movement accounts: $movementAccounts")
        for (subsidiary in sumasSaldosRequestDto.subsidiaries){
            val subsidiaryEntity = subsidiaryDao.getSubsidiaryById(subsidiary)
            val areaSumas = mutableListOf<AreaSumas>()
            for (area in sumasSaldosRequestDto.areas){
                var totalSumsDebitAmount = BigDecimal.ZERO
                var totalSumsCreditAmount = BigDecimal.ZERO
                var totalBalancesDebitAmount = BigDecimal.ZERO
                var totalBalancesCreditAmount = BigDecimal.ZERO
                val areaEntity = areaDao.getAreaById(area)
                val accountSumas = mutableListOf<AccountSumas>()
                for (accountCode in rootAccounts){
                    for (movementAccount in movementAccounts){
                        if (movementAccount.get(0).toString() == accountCode.toString()){
                            logger.info("Obteniendo id de la cuenta $movementAccount")
                            val accountId = accountDao.getAccountIdBYCode(movementAccount, companyId)
                            val accountEntity = accountDao.getAccountById(accountId)
                            logger.info("Obteniendo transacciones de la cuenta ${accountEntity.accountId}")
                            val areaSubsidiaryId= areaSubsidiaryDao.findAreaSubsidiaryId(subsidiaryEntity.subsidiaryId, areaEntity.areaId)
                            val transactions = transactionDao.getLedgerTransactions(companyId, accountEntity.accountId,
                                areaSubsidiaryId, formatDataClass.stringToDateAtBeginOfDay(sumasSaldosRequestDto.from),
                                formatDataClass.stringToDateAtEndOfDay(sumasSaldosRequestDto.to), sumasSaldosRequestDto.currencies).map{
                                TransactionLedger(it.voucherCode, it.registrationDate, it.transactionType, it.glosaDetail, it.documentNumber, it.debitAmount, it.creditAmount, it.balances)
                            }
                            logger.info("Transactions: $transactions")
                            val totalDebit = transactions.sumOf { it.debitAmount }
                            logger.info("Total debit: $totalDebit")
                            val totalCredit = transactions.sumOf { it.creditAmount }
                            val totalBalances = totalDebit.subtract(totalCredit)
                            if (totalBalances >= BigDecimal.ZERO){
                                accountSumas.add(AccountSumas(accountEntity.codeAccount, accountEntity.nameAccount, totalDebit, totalCredit, totalBalances, BigDecimal.ZERO))
                                totalBalancesDebitAmount += totalBalances.abs()
                            }
                            else{
                                accountSumas.add(AccountSumas(accountEntity.codeAccount, accountEntity.nameAccount, totalDebit, totalCredit, BigDecimal.ZERO, totalBalances.abs()))
                                totalBalancesCreditAmount += totalBalances.abs()
                            }
                            totalSumsDebitAmount += totalDebit
                            totalSumsCreditAmount += totalCredit
                            logger.info("Total debit: $totalBalancesDebitAmount")
                            logger.info("Total credit: $totalBalancesCreditAmount")
                            logger.info("Total sums debit: $totalSumsDebitAmount")
                            logger.info("Total sums credit: $totalSumsCreditAmount")
                        }
                    }
                }
                areaSumas.add(AreaSumas(areaEntity.areaId, subsidiaryEntity.subsidiaryId, areaEntity.areaName, accountSumas, totalSumsDebitAmount, totalSumsCreditAmount, totalBalancesDebitAmount, totalBalancesCreditAmount))
            }
            subsidiarySumas.add(SubsidiarySumas(subsidiaryEntity.subsidiaryId, subsidiaryEntity.subsidiaryName, areaSumas))
        }
        return SumasSaldosResponseDto(
            company.companyName, formatDataClass.stringToDateAtBeginOfDay(sumasSaldosRequestDto.from),
            formatDataClass.stringToDateAtEndOfDay(sumasSaldosRequestDto.to), currencyName.moneyName, subsidiarySumas)
    }

    suspend fun getSumasSaldosPdf(companyId: Int, sumasSaldosRequestDto: SumasSaldosRequestDto, header: Map<String, String>): SumasSaldosResponseDtoPdf {
        logger.info("Obteniendo sumas y saldos")
        val company = companyDao.getCompanyById(companyId)
        val url = companyBl.getUrlImageByCompanyId(companyId)
        val currencyName = exchangeMoneyBl.getExchangeMoneyByCompanyIdAndISO(companyId, sumasSaldosRequestDto.currencies)
        val subsidiarySumas = mutableListOf<SubsidiarySumasPdf>()
        val rootAccounts = accountDao.getRootAccounts(companyId)
        logger.info("Root accounts: $rootAccounts")
        val movementAccounts = accountDao.getMovementAccounts(companyId)
        logger.info("Movement accounts: $movementAccounts")
        for (subsidiary in sumasSaldosRequestDto.subsidiaries){
            val subsidiaryEntity = subsidiaryDao.getSubsidiaryById(subsidiary)
            val areaSumas = mutableListOf<AreaSumasPdf>()
            for (area in sumasSaldosRequestDto.areas){
                var totalSumsDebitAmount = BigDecimal.ZERO
                var totalSumsCreditAmount = BigDecimal.ZERO
                var totalBalancesDebitAmount = BigDecimal.ZERO
                var totalBalancesCreditAmount = BigDecimal.ZERO
                val areaEntity = areaDao.getAreaById(area)
                val accountSumas = mutableListOf<AccountSumasPdf>()
                for (accountCode in rootAccounts){
                    for (movementAccount in movementAccounts){
                        if (movementAccount[0].toString() == accountCode.toString()){
                            logger.info("Obteniendo id de la cuenta $movementAccount")
                            val accountId = accountDao.getAccountIdBYCode(movementAccount, companyId)
                            val accountEntity = accountDao.getAccountById(accountId)
                            logger.info("Obteniendo transacciones de la cuenta ${accountEntity.accountId}")
                            val areaSubsidiaryId= areaSubsidiaryDao.findAreaSubsidiaryId(subsidiaryEntity.subsidiaryId, areaEntity.areaId)
                            val transactions = transactionDao.getLedgerTransactions(companyId, accountEntity.accountId,
                                areaSubsidiaryId, formatDataClass.stringToDateAtBeginOfDay(sumasSaldosRequestDto.from),
                                formatDataClass.stringToDateAtEndOfDay(sumasSaldosRequestDto.to), sumasSaldosRequestDto.currencies).map{
                                TransactionLedger(it.voucherCode, it.registrationDate, it.transactionType, it.glosaDetail, it.documentNumber, it.debitAmount, it.creditAmount, it.balances)
                            }
                            logger.info("Transactions: $transactions")
                            val totalDebit = transactions.sumOf { it.debitAmount }
                            logger.info("Total debit: $totalDebit")
                            val totalCredit = transactions.sumOf { it.creditAmount }
                            val totalBalances = totalDebit.subtract(totalCredit)
                            if (totalBalances >= BigDecimal.ZERO){
                                accountSumas.add(AccountSumasPdf(accountEntity.codeAccount, accountEntity.nameAccount,
                                    formatDataClass.getNumber(totalDebit), formatDataClass.getNumber(totalDebit), formatDataClass.getNumber(totalDebit), formatDataClass.getNumber(BigDecimal.ZERO)))
                                totalBalancesDebitAmount += totalBalances.abs()
                            }
                            else{
                                accountSumas.add(AccountSumasPdf(accountEntity.codeAccount, accountEntity.nameAccount,
                                    formatDataClass.getNumber(totalDebit), formatDataClass.getNumber(totalDebit), formatDataClass.getNumber(BigDecimal.ZERO), formatDataClass.getNumber(totalBalances.abs())))
                                totalBalancesCreditAmount += totalBalances.abs()
                            }
                            totalSumsDebitAmount += totalDebit
                            totalSumsCreditAmount += totalCredit
                        }
                    }
                }
                areaSumas.add(AreaSumasPdf(areaEntity.areaId, subsidiaryEntity.subsidiaryId, areaEntity.areaName, accountSumas,
                    formatDataClass.getNumber(totalSumsDebitAmount), formatDataClass.getNumber(totalSumsCreditAmount), formatDataClass.getNumber(totalBalancesDebitAmount), formatDataClass.getNumber(totalBalancesCreditAmount)))
            }
            subsidiarySumas.add(SubsidiarySumasPdf(subsidiaryEntity.subsidiaryId, subsidiaryEntity.subsidiaryName, areaSumas))
        }

        val date = LocalDateTime.now()
        val userEntity = accessPersonBl.getAccessPersonInformationByToken(header["authorization"]!!.substring(7))
        val userName = userEntity!!.firstName + " " + userEntity!!.lastName
        return SumasSaldosResponseDtoPdf(
            company.companyName,
            url,
            formatDataClass.getDateFromLocalDateTime(date),
            formatDataClass.getHourFromLocalDateTime(date),
            userName,
            formatDataClass.changeFormatStringDate(sumasSaldosRequestDto.from),
            formatDataClass.changeFormatStringDate(sumasSaldosRequestDto.to),
            currencyName.moneyName,
            subsidiarySumas)
    }





}