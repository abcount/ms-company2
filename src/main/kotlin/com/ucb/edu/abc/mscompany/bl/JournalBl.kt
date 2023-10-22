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
        private val areaDao: AreaDao
){
    private val logger: Logger = LoggerFactory.getLogger(CompanyBl::class.java)
    fun getJournal(companyId: Int, journalRequestDto: JournalRequestDto): JournalResponseDto {
        logger.info("Obteniendo libro diario")
        val subsidiaryDtoList = mutableListOf<SubsidiaryDto>()

        for (subsidiaryId in journalRequestDto.subsidiaries) {
            val subsidiaryEntity = subsidiaryDao.getSubsidiaryById(subsidiaryId)
            val areaDtoList = mutableListOf<AreaDto>()

            for (areaId in journalRequestDto.areas) {
                val areaEntity = areaDao.getAreaById(areaId)
                val transactions = transactionDao.getTransactionForAreaAndSubsidiary(companyId, areaId, subsidiaryId, journalRequestDto.from, journalRequestDto.to)
                val transactionDtoList= transformToTransactionDtoList(transactions)


                areaDtoList.add(AreaDto(areaEntity.areaId, areaEntity.areaName, transactionDtoList))
            }

            subsidiaryDtoList.add(SubsidiaryDto(subsidiaryEntity.subsidiaryId, subsidiaryEntity.subsidiaryName, areaDtoList))
        }

        return JournalResponseDto(subsidiaryDtoList)
    }

    private fun transformToAccountDtoList(transactionId: Long): List<AccountDto> {
        val transactionAccounts = transactionDao.getAccountDetailsByTransactionId(transactionId)

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

    private fun transformToTransactionDtoList(transactions: List<TransactionEntity>): List<TransactionDto> {
        val transactionDtoList = mutableListOf<TransactionDto>()
        for (i in transactions) {
            transactionDtoList.add(TransactionDto(
                    i.transactionNumber,
                    transactionTypeDao.getTransactionTypeNameById(i.transactionTypeId),
                    i.date,
                    BigDecimal(0.0),
                    i.glosaGeneral,
                    transformToAccountDtoList(i.transactionId)
            )
            )

        }
        return transactionDtoList

    }


}