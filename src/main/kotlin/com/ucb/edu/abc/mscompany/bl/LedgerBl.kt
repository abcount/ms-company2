package com.ucb.edu.abc.mscompany.bl
import com.ucb.edu.abc.mscompany.config.FormatDataClass
import com.ucb.edu.abc.mscompany.dao.*
import com.ucb.edu.abc.mscompany.dto.request.LedgerRequestDto
import com.ucb.edu.abc.mscompany.dto.response.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class LedgerBl @Autowired constructor(
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

){
    private val logger: Logger = LoggerFactory.getLogger(CompanyBl::class.java)

    /*fun getLedger(companyId: Int, ledgerRequestDto: LedgerRequestDto): LedgerResponseDto {
        logger.info("Obteniendo libro mayor")
        val company = companyDao.getCompanyById(companyId)
        val currencyName = exchangeRateDao.getExchangeRateById(ledgerRequestDto.currencies)
        val subsidiaryLedger= mutableListOf<SubsidiaryLedger>()
        for (subsidiary in ledgerRequestDto.subsidiaries){
            val subsidiaryEntity = subsidiaryDao.getSubsidiaryById(subsidiary)
            val areaLedger = mutableListOf<AreaLedger>()
            for (area in ledgerRequestDto.areas){
                val areaEntity = accountDao.getAccountById(area)
                val accountLedger = mutableListOf<AccountLedger>()
                 for (account in ledgerRequestDto.accountsId){
                      val accountEntity = accountDao.getAccountById(account)
                      val transactionLedger = mutableListOf<TransactionLedger>()
                      accountLedger.add(AccountLedger(accountEntity.codeAccount, accountEntity.nameAccount, transactionLedger,  ))

                 }


            }
        }


        return LedgerResponseDto(company.companyName, ledgerRequestDto.from, ledgerRequestDto.to, currencyName.moneyName,subsidiaryLedger)
    }
*/
    fun getLedger(companyId: Int, ledgerRequestDto: LedgerRequestDto): LedgerResponseDto {
        logger.info("Obteniendo libro mayor")
        val company = companyDao.getCompanyById(companyId)
        val currencyName = exchangeMoneyBl.getExchangeMoneyByCompanyIdAndISO(companyId, ledgerRequestDto.currencies)
        val subsidiaryLedger= mutableListOf<SubsidiaryLedger>()

        for (subsidiary in ledgerRequestDto.subsidiaries){
            val subsidiaryEntity = subsidiaryDao.getSubsidiaryById(subsidiary)
            val areaLedger = mutableListOf<AreaLedger>()

            for (area in ledgerRequestDto.areas){
                val areaEntity = areaDao.getAreaById(area)
                val accountLedger = mutableListOf<AccountLedger>()

                for (account in ledgerRequestDto.accountsId){
                    val accountEntity = accountDao.getAccountById(account)
                    val areaSubsidiaryId= areaSubsidiaryDao.findAreaSubsidiaryId(subsidiaryEntity.subsidiaryId, areaEntity.areaId)

                    val transactions = transactionDao.getLedgerTransactions(companyId, account, areaSubsidiaryId,
                        formatDataClass.stringToDateAtBeginOfDay(ledgerRequestDto.from),
                        formatDataClass.stringToDateAtEndOfDay(ledgerRequestDto.to), ledgerRequestDto.currencies)


                    val totalDebit = transactions.sumOf { it.debitAmount }
                    val totalCredit = transactions.sumOf { it.creditAmount }
                    val totalBalances = totalDebit.subtract(totalCredit)

                    accountLedger.add(AccountLedger(accountEntity.codeAccount, accountEntity.nameAccount, transactions, totalDebit, totalCredit, totalBalances))
                }

                areaLedger.add(AreaLedger(areaEntity.areaId, areaEntity.areaName, accountLedger))
            }

            subsidiaryLedger.add(SubsidiaryLedger(subsidiaryEntity.subsidiaryId, subsidiaryEntity.subsidiaryName, areaLedger))
        }

        return LedgerResponseDto(company.companyName, formatDataClass.stringToDateAtBeginOfDay(ledgerRequestDto.from),
            formatDataClass.stringToDateAtEndOfDay(ledgerRequestDto.to), currencyName.moneyName, subsidiaryLedger)
    }

    suspend fun getLedgerPdf(companyId: Int, ledgerRequestDto: LedgerRequestDto, header: Map<String, String>): LedgerResponseDtoPdf {
        logger.info("Obteniendo libro mayor")
        val company = companyDao.getCompanyById(companyId)
        val url = companyBl.getUrlImageByCompanyId(companyId)
        val currencyName = exchangeMoneyBl.getExchangeMoneyByCompanyIdAndISO(companyId, ledgerRequestDto.currencies)
        val subsidiaryLedger= mutableListOf<SubsidiaryLedgerPdf>()

        for (subsidiary in ledgerRequestDto.subsidiaries){
            val subsidiaryEntity = subsidiaryDao.getSubsidiaryById(subsidiary)
            val areaLedger = mutableListOf<AreaLedgerPdf>()

            for (area in ledgerRequestDto.areas){
                val areaEntity = areaDao.getAreaById(area)
                val accountLedger = mutableListOf<AccountLedgerPdf>()

                for (account in ledgerRequestDto.accountsId){
                    val accountEntity = accountDao.getAccountById(account)
                    val areaSubsidiaryId= areaSubsidiaryDao.findAreaSubsidiaryId(subsidiaryEntity.subsidiaryId, areaEntity.areaId)


                    val transactions = transactionDao.getLedgerTransactions(companyId, account, areaSubsidiaryId,
                        formatDataClass.stringToDateAtBeginOfDay(ledgerRequestDto.from),
                        formatDataClass.stringToDateAtEndOfDay(ledgerRequestDto.to), ledgerRequestDto.currencies).map{
                        TransactionLedger(it.voucherCode, it.registrationDate, it.transactionType, it.glosaDetail, it.documentNumber, it.debitAmount, it.creditAmount, it.balances)
                    }

                    val totalDebit = transactions.sumOf { it.debitAmount }
                    val totalCredit = transactions.sumOf { it.creditAmount }
                    val totalBalances = totalDebit.subtract(totalCredit)

                    val transactionPdf = transactions.map {
                        TransactionLedgerPdf(it.voucherCode, formatDataClass.convertDateToString(it.registrationDate),
                            it.transactionType, it.glosaDetail, it.documentNumber,
                            formatDataClass.getNumber(it.debitAmount), formatDataClass.getNumber(it.creditAmount), formatDataClass.getNumber(it.balances))
                    }

                    if(transactions.isNotEmpty()){
                        accountLedger.add(AccountLedgerPdf(accountEntity.codeAccount, accountEntity.nameAccount, transactionPdf,
                            formatDataClass.getNumber(totalDebit), formatDataClass.getNumber(totalCredit), formatDataClass.getNumber(totalCredit)))
                    }

                }

                if(accountLedger.isNotEmpty()){
                    areaLedger.add(AreaLedgerPdf(areaEntity.areaId, areaEntity.areaName, accountLedger))
                }
            }

            subsidiaryLedger.add(SubsidiaryLedgerPdf(subsidiaryEntity.subsidiaryId, subsidiaryEntity.subsidiaryName, areaLedger))
        }
        val date = LocalDateTime.now()
        val userEntity = accessPersonBl.getAccessPersonInformationByToken(header["authorization"]!!.substring(7))
        val userName = userEntity!!.firstName + " " + userEntity!!.lastName

        return LedgerResponseDtoPdf(
            company.companyName,
            formatDataClass.changeFormatStringDate(ledgerRequestDto.from),
            url,
            formatDataClass.getDateFromLocalDateTime(date),
            formatDataClass.getHourFromLocalDateTime(date),
            userName,
            formatDataClass.changeFormatStringDate(ledgerRequestDto.to),
            currencyName.moneyName,
            subsidiaryLedger)
    }





}