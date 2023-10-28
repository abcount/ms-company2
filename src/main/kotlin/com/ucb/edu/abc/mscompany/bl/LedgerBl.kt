package com.ucb.edu.abc.mscompany.bl
import com.ucb.edu.abc.mscompany.dao.*
import com.ucb.edu.abc.mscompany.dto.request.LedgerRequestDto
import com.ucb.edu.abc.mscompany.dto.response.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LedgerBl @Autowired constructor(
        private val transactionDao: TransactionDao,
        private val companyDao: CompanyDao,
        private val subsidiaryDao: SubsidiaryDao,
        private val accountDao: AccountDao,
        private val exchangeRateDao: ExchangeRateDao,
        private val areaDao: AreaDao,
        private val areaSubsidiaryDao: AreaSubsidiaryDao

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
        val currencyName = exchangeRateDao.getExchangeRateById(ledgerRequestDto.currencies)
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


                    val transactions = transactionDao.getLedgerTransactions(companyId, account, areaSubsidiaryId, ledgerRequestDto.from, ledgerRequestDto.to, ledgerRequestDto.currencies)


                    val totalDebit = transactions.sumOf { it.debitAmount }
                    val totalCredit = transactions.sumOf { it.creditAmount }
                    val totalBalances = totalDebit.subtract(totalCredit)

                    accountLedger.add(AccountLedger(accountEntity.codeAccount, accountEntity.nameAccount, transactions, totalDebit, totalCredit, totalBalances))
                }

                areaLedger.add(AreaLedger(areaEntity.areaId, areaEntity.areaName, accountLedger))
            }

            subsidiaryLedger.add(SubsidiaryLedger(subsidiaryEntity.subsidiaryId, subsidiaryEntity.subsidiaryName, areaLedger))
        }

        return LedgerResponseDto(company.companyName, ledgerRequestDto.from, ledgerRequestDto.to, currencyName.moneyName, subsidiaryLedger)
    }

}