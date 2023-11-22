package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.config.FormatDataClass
import com.ucb.edu.abc.mscompany.dao.*
import com.ucb.edu.abc.mscompany.dto.request.JournalRequestDto
import com.ucb.edu.abc.mscompany.dto.response.*
import com.ucb.edu.abc.mscompany.entity.TransactionEntity
import com.ucb.edu.abc.mscompany.enums.UserAbcCategory
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
class JournalBl @Autowired constructor(
        private val transactionDao: TransactionDao,
        private val transactionTypeDao: TransactionTypeDao,
        private val subsidiaryDao: SubsidiaryDao,
        private val companyDao: CompanyDao,
        private val areaDao: AreaDao,
        private val exchangeMoneyBl: ExchangeMoneyBl,
        private val exchangeRateBl: ExchangeRateBl,
        private val companyBl: CompanyBl,
        private val transactionTypeBl: TransactionTypeBl,
    private val formatDataClass: FormatDataClass,
    private val accessPersonBl: AccessPersonBl
){
    private val logger: Logger = LoggerFactory.getLogger(CompanyBl::class.java)
    fun getJournal(companyId: Int, journalRequestDto: JournalRequestDto): JournalResponseDto {
        logger.info("Obteniendo libro diario de la empresa: $companyId")
        val companyEntity = companyDao.getCompanyById(companyId)
        //Buscar el tipo de moneda

        val exchangeMoney = exchangeMoneyBl.getExchangeMoneyByCompanyIdAndISO(companyId, journalRequestDto.currencies)

        val subsidiaryDtoList = mutableListOf<SubsidiaryDto>()

        for (subsidiaryId in journalRequestDto.subsidiaries) {
            val subsidiaryEntity = subsidiaryDao.getSubsidiaryById(subsidiaryId)
            val areaDtoList = mutableListOf<AreaDto>()

            for (areaId in journalRequestDto.areas) {
                val areaEntity = areaDao.getAreaById(areaId)
                val transactions = transactionDao.getTransactionForAreaAndSubsidiary(companyId,  subsidiaryId,areaId, journalRequestDto.from, journalRequestDto.to, journalRequestDto.transactionType)
                val transactionDtoList= transformToTransactionDtoList(transactions,exchangeMoney.abbreviationName)


                areaDtoList.add(AreaDto(areaEntity.areaId, areaEntity.areaName, transactionDtoList))
            }

            subsidiaryDtoList.add(SubsidiaryDto(subsidiaryEntity.subsidiaryId, subsidiaryEntity.subsidiaryName, areaDtoList))
        }

        return JournalResponseDto(companyEntity.companyName,journalRequestDto.from,journalRequestDto.to,exchangeMoney.moneyName,subsidiaryDtoList)
    }

    private fun transformToAccountDtoList(transactionId: Long, exchangeMoneyIso: String): List<AccountDto> {
        val transactionAccounts = transactionDao.getAccountDetailsByTransactionId(transactionId, exchangeMoneyIso)
        return transactionAccounts.map {
            AccountDto(
                    it.codeAccount,
                    it.nameAccount,
                    it.glosaDetail,
                    it.debitAmount,
                    it.creditAmount
            )
        }
    }

    private fun transformToTransactionDtoList(transactions: List<TransactionEntity>, exchangeMoneyIso: String): List<TransactionDto> {
        val transactionDtoList = mutableListOf<TransactionDto>()
        for (i in transactions) {
            val accountDto = transformToAccountDtoList(i.transactionId, exchangeMoneyIso)
            val totalDebit = accountDto.sumOf { it.debitAmount }
            val totalCredit = accountDto.sumOf { it.creditAmount }
            transactionDtoList.add(TransactionDto(
                    i.transactionNumber,
                    transactionTypeDao.getTransactionTypeNameById(i.transactionTypeId.toInt()),
                    i.date,
                    BigDecimal(0.0),  // Exchange rate value
                    i.glosaGeneral,
                    accountDto,
                    totalDebit,
                    totalCredit
            ))
            /*
            transactionDtoList.add(TransactionDto(
                    i.transactionNumber,
                    transactionTypeDao.getTransactionTypeNameById(i.transactionTypeId),
                    i.date,
                    BigDecimal(0.0),
                    i.glosaGeneral,
                    transformToAccountDtoList(i.transactionId, exchangeRateId)
            )
            )*/

        }
        return transactionDtoList
    }

    suspend fun getJournalForPDF(companyId: Int, journalRequestDto: JournalRequestDto, header: Map<String,String>): JournalResponseDtoPdf{
        logger.info("Obteniendo libro diario de la empresa: $companyId")
        val companyEntity = companyDao.getCompanyById(companyId)
        val url = companyBl.getUrlImageByCompanyId(companyId)
        val exchangeMoney = exchangeMoneyBl.getExchangeMoneyByCompanyIdAndISO(companyId, journalRequestDto.currencies)
        val subsidiaryDtoList = mutableListOf<SubsidiaryDtoPDF>()
        var type = transactionTypeBl.getTransactionTypeNameById(journalRequestDto.transactionType)
        for (subsidiaryId in journalRequestDto.subsidiaries) {
            val subsidiaryEntity = subsidiaryDao.getSubsidiaryById(subsidiaryId)
            val areaDtoList = mutableListOf<AreaDtoPDF>()

            for (areaId in journalRequestDto.areas) {
                val areaEntity = areaDao.getAreaById(areaId)
                val transactions = transactionDao.getTransactionForAreaAndSubsidiary(companyId,  subsidiaryId,areaId, journalRequestDto.from, journalRequestDto.to, journalRequestDto.transactionType)
                var transactionDtoList = mutableListOf<TransactionDtoPDF>()
                if(transactions.isNotEmpty()){
                    transactionDtoList= transformToTransactionDtoListPDF(transactions,exchangeMoney.abbreviationName)
                }
                if(transactionDtoList.isNotEmpty()){
                    areaDtoList.add(AreaDtoPDF(areaEntity.areaId, areaEntity.areaName, transactionDtoList))
                }
            }

            if(areaDtoList.isNotEmpty()){
                subsidiaryDtoList.add(SubsidiaryDtoPDF(subsidiaryEntity.subsidiaryId, subsidiaryEntity.subsidiaryName, areaDtoList))
            }

        }

        val date = LocalDateTime.now()
        val userEntity = accessPersonBl.getAccessPersonInformationByToken(header["authorization"]!!.substring(7))
        val userName = userEntity!!.firstName + " " + userEntity!!.lastName

        return JournalResponseDtoPdf(
            companyEntity.companyName,
            formatDataClass.convertDateToString(journalRequestDto.from),
            url,
            userName,
            formatDataClass.getDateFromLocalDateTime(date),
            formatDataClass.getHourFromLocalDateTime(date),
            type,
            formatDataClass.convertDateToString(journalRequestDto.to),
            exchangeMoney.moneyName,
            subsidiaryDtoList)
    }

    private fun transformToTransactionDtoListPDF(transactions: List<TransactionEntity>, exchangeMoneyIso: String): MutableList<TransactionDtoPDF> {
        val transactionDtoList = mutableListOf<TransactionDtoPDF>()
        for (i in transactions) {
            val accountDto = transformToAccountDtoList(i.transactionId, exchangeMoneyIso)
            val exchangeRateEntity = exchangeRateBl.getExchangeRate(i.exchangeRateId)
            val totalDebit = accountDto.sumOf { it.debitAmount }
            val totalCredit = accountDto.sumOf { it.creditAmount }
            val accountDtoPDF = accountDto.map {
                AccountDtoPDF(
                    it.codeAccount,
                    it.nameAccount,
                    it.glosaDetail,
                    formatDataClass.getNumber(it.debitAmount),
                    formatDataClass.getNumber(it.creditAmount)
                )
            }
            transactionDtoList.add(TransactionDtoPDF(
                i.transactionNumber,
                transactionTypeDao.getTransactionTypeNameById(i.transactionTypeId.toInt()),
                formatDataClass.convertLocalDateTimeToString(i.date),
                formatDataClass.getNumber(exchangeRateEntity.currency),  // Exchange rate value
                i.glosaGeneral,
                accountDtoPDF,
                formatDataClass.getNumber(totalDebit),
                formatDataClass.getNumber(totalCredit)
            ))
        }
        return transactionDtoList

    }










}