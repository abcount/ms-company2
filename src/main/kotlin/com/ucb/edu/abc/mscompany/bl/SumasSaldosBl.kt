package com.ucb.edu.abc.mscompany.bl

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
    private val exchangeMoneyBl: ExchangeMoneyBl
) {
    private val logger: Logger = LoggerFactory.getLogger(CompanyBl::class.java)

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
                            val transactions = transactionDao.getLedgerTransactions(companyId, accountEntity.accountId, areaSubsidiaryId, sumasSaldosRequestDto.from, sumasSaldosRequestDto.to, sumasSaldosRequestDto.currencies).map{
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


        return SumasSaldosResponseDto(company.companyName, sumasSaldosRequestDto.from, sumasSaldosRequestDto.to, currencyName.moneyName, subsidiarySumas)
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