package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.*
import com.ucb.edu.abc.mscompany.dto.request.JournalRequestDto
import com.ucb.edu.abc.mscompany.dto.response.*
import com.ucb.edu.abc.mscompany.entity.TransactionEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class JournalBl @Autowired constructor(
        private val transactionDao: TransactionDao,
        private val transactionTypeDao: TransactionTypeDao,
        private val subsidiaryDao: SubsidiaryDao,
        private val companyDao: CompanyDao,
        private val areaDao: AreaDao,
        private val exchangeRateDao: ExchangeRateDao,
        private val exchangeMoneyBl: ExchangeMoneyBl
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
                    transactionTypeDao.getTransactionTypeNameById(i.transactionTypeId),
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


}