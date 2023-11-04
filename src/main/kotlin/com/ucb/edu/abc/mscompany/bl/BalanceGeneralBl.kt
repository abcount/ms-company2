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
import java.util.*

@Service
class BalanceGeneralBl @Autowired constructor(
        private val transactionDao: TransactionDao,
        private val transactionTypeDao: TransactionTypeDao,
        private val subsidiaryDao: SubsidiaryDao,
        private val companyDao: CompanyDao,
        private val areaDao: AreaDao,
        private val exchangeRateDao: ExchangeRateDao,
        private val accountDao: AccountDao,
) {

    private val logger: Logger = LoggerFactory.getLogger(CompanyBl::class.java)
    fun getBalanceGeneral(companyId: Int, balanceGeneralRequestDto: BalanceGeneralRequestDto): BalanceGeneralResponseDto {
        logger.info("Obteniendo balance general de la empresa: $companyId")
        val companyEntity = companyDao.getCompanyById(companyId)
        val exchangeRate = exchangeRateDao.getExchangeRateById(balanceGeneralRequestDto.currencies)
        val subsidiaryBalanceDtoList= mutableListOf<SubsidiaryBalance>()
        for (subsidiaryId in balanceGeneralRequestDto.subsidiaries) {
            val subsidiaryEntity = subsidiaryDao.getSubsidiaryById(subsidiaryId)
            var areaBalanceDtoList = mutableListOf<AreaBalance>()
            for (areaId in balanceGeneralRequestDto.areas) {
                var accountBalanceDtoList = mutableListOf<AccountBalance>()

                val areaEntity = areaDao.getAreaById(areaId)
                val listAccountBalanced= getAccountBalance(companyId, balanceGeneralRequestDto.currencies, balanceGeneralRequestDto.to)
                areaBalanceDtoList.add(AreaBalance(companyId,areaEntity.areaId, areaEntity.areaName, listAccountBalanced,
                        BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO ))
            }

            subsidiaryBalanceDtoList.add(SubsidiaryBalance(subsidiaryEntity.subsidiaryId, subsidiaryEntity.subsidiaryName, areaBalanceDtoList))
        }

        return BalanceGeneralResponseDto(companyEntity.companyName, balanceGeneralRequestDto.to, exchangeRate.moneyName, subsidiaryBalanceDtoList)
    }

    fun getAccountBalance(companyId: Int, exchangeId: Int, to: Date): List<AccountBalance>{
        var accountBalanceDtoList = mutableListOf<AccountBalance>()
        var activeAccountId= accountDao.getAccountIdBYCode("1", companyId)
        var passiveAccountId= accountDao.getAccountIdBYCode("2", companyId)
        var patrimonyAccountId= accountDao.getAccountIdBYCode("3", companyId)

        val activeAccountTree = buildAccountTree(activeAccountId, companyId, to)
        val passiveAccountTree = buildAccountTree(passiveAccountId, companyId,to)
        val patrimonyAccountTree = buildAccountTree(patrimonyAccountId, companyId,to)
        accountBalanceDtoList.add(activeAccountTree)
        accountBalanceDtoList.add(passiveAccountTree)
        accountBalanceDtoList.add(patrimonyAccountTree)

        return accountBalanceDtoList


    }

    /*private fun buildAccountTree(rootAccountId: Int, companyId: Int): AccountBalance {
        // Obten todas las cuentas de la compañía
        val allAccounts = accountDao.getAccountPlanByCompanyId(companyId).associateBy { it.accountId }

        // Función recursiva para construir el árbol
        fun buildTree(accountId: Int): AccountBalance {
            val currentAccount = allAccounts[accountId] ?: throw IllegalStateException("Account not found for ID: $accountId")
            val children = allAccounts.values
                    .filter { it.accountAccountId == accountId }
                    .map { buildTree(it.accountId) }

            val amount = BigDecimal.ZERO
            return AccountBalance(currentAccount.codeAccount, currentAccount.nameAccount, amount, children)
        }

        return buildTree(rootAccountId)
    }
*/
    private fun buildAccountTree(rootAccountId: Int, companyId: Int,to: Date): AccountBalance {
        // Obtenemos todas las cuentas de la compañía
        val allAccounts = accountDao.getAccountPlanByCompanyId(companyId).associateBy { it.accountId }

        // Función recursiva para construir el árbol y sumar montos
        fun buildTree(accountId: Int): AccountBalance {
            val currentAccount = allAccounts[accountId] ?: throw IllegalStateException("Account not found for ID: $accountId")
            val children = allAccounts.values
                    .filter { it.accountAccountId == accountId }
                    .map { buildTree(it.accountId) }

            // Calculamos el monto para la cuenta actual. Si es una hoja, obtenemos el balance. Si es un nodo padre, sumamos el de los hijos.
            val amount = if (children.isEmpty()) {
                // Si no tiene hijos, es una cuenta hoja con transacciones, obtenemos su monto.
                accountDao.getBalanceByAccount(accountId,to) ?: BigDecimal.ZERO
            } else {
                // Si tiene hijos, es un nodo agrupador, sumamos el monto de los hijos.
                children.fold(BigDecimal.ZERO) { total, child -> total.add(child.amount) }
            }

            return AccountBalance(currentAccount.codeAccount, currentAccount.nameAccount, amount, children)
        }

        return buildTree(rootAccountId)
    }

}