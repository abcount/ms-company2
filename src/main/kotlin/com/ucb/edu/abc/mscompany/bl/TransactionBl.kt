package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.*
import com.ucb.edu.abc.mscompany.dto.request.TransactionAccountDto
import com.ucb.edu.abc.mscompany.dto.request.TransactionDto
import com.ucb.edu.abc.mscompany.dto.response.*
import com.ucb.edu.abc.mscompany.entity.DebitCreditEntity
import com.ucb.edu.abc.mscompany.entity.TransactionAccountEntity
import com.ucb.edu.abc.mscompany.entity.TransactionEntity
import com.ucb.edu.abc.mscompany.enums.UserAbcCategory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class TransactionBl @Autowired constructor(
        private val transactionDao: TransactionDao,
        private val transactionAccountDao: TransactionAccountDao,
        private val debitCreditDao: DebitCreditDao,
        private val areaSubsidiaryDao: AreaSubsidiaryDao,
        private val transactionTypeBl: TransactionTypeBl,
        private val userBl: UserBl,
        private val subsidiaryBl: SubsidiaryBl,
        private val subsidiaryDao: SubsidiaryDao,
        private val areaBl: AreaBl,
        private val areaDao: AreaDao,
        private val exchangeMoneyBl: ExchangeMoneyBl,
        private val accountBl: AccountBl,
        private val auxiliaryAccountBl: AuxiliaryAccountBl,
        private val companyBl: CompanyBl,


){
    private val logger: Logger = LoggerFactory.getLogger(CompanyBl::class.java)

    fun saveTransaction(companyId: Int, transactionDto: TransactionDto, headers: Map<String, String>){
        val tokenAuth =  headers["authorization"]!!.substring(7)
        val userId = userBl.getUserIdByCompanyIdAndToken (tokenAuth,   companyId, UserAbcCategory.ACTIVE,null)

        logger.info("Guardando transaccion")
        if (transactionDto.totalCredit != transactionDto.totalDebit){
            throw Exception("El total de creditos y debitos no coinciden")
        }
        val transactionEntity = factoryTransaction(transactionDto, companyId, userId)
        transactionDao.create(transactionEntity)

        //registrar en transaction Account
        transactionDto.transactions.forEach {
            val transactionAccountEntity = factoryTransactionAccount(it)
            transactionAccountEntity.transactionId = transactionEntity.transactionId
            transactionAccountEntity.companyId = companyId

            transactionAccountDao.create(transactionAccountEntity)
            val debitCreditEntity = factoryCreditDebit(it, transactionAccountEntity.transactionAccountId, transactionDto.currencyId)
            debitCreditDao.create(debitCreditEntity)
        }
    }


    fun getDataForDoATransaction(companyId: Int, headers: Map<String,String>): TransactionalVoucherDto{
        val transactionalVoucherDto = TransactionalVoucherDto()
        val tokenAuth =  headers["authorization"]!!.substring(7)
        println("tokenAuth: $tokenAuth")
        val userId = userBl.getUserIdByCompanyIdAndToken (tokenAuth,   companyId, UserAbcCategory.ACTIVE,null)

        //Obteniendo los datos sueltos del response
        transactionalVoucherDto.transactionNumber = (transactionDao.getLastTransactionNumber(companyId)?:0) + 1
        transactionalVoucherDto.companyName = companyBl.getCompanyName(companyId)

        //Obteniendo la lista de transactionType
        transactionalVoucherDto.transactionType = transactionTypeBl.getAllTransactionType()

        //Obtener Subsidiaries con permisos
        //TODO: verificar que la funcion de abajo funciona

        val subsidiaryList = subsidiaryDao.getSubsidiariesByUserIdAndCompanyId(userId, companyId).map{
            Subsidiary(it.subsidiaryId, it.subsidiaryName, it.address?:"", false)
        }

        /*
        val subsidiaryList = subsidiaryBl.getByCompanyId(companyId).map{
            Subsidiary(it.subsidiaryId, it.subsidiaryName, it.address?:"", false)
        }*/
        transactionalVoucherDto.subsidiaries = subsidiaryList

        //Obtener Areas con permisos
        //TODO: verificar que la funcion de abajo funciona
        val areaList = areaDao.getAreasByUserIdAndCompanyId(userId, companyId).map {
            Area(it.areaId, it.areaName, false)
        }

        /*
        val areaList = areaBl.getAreasByCompanyId(companyId).map {
            Area(it.areaId, it.areaName, false)
        }*/
        transactionalVoucherDto.areas = areaList

        //Obtener tipos de cambio
        val exchangeList = exchangeMoneyBl.getAllCurrenciesByCompanyId(companyId).map {
            CurrencyVoucher(
                it.exchangeMoneyId,
                it.moneyName,
                it.abbreviationName,
            )
        }
        transactionalVoucherDto.currencies = exchangeList

        //Obtener cuentas de movimiento
        val accountList = accountBl.getAccountsNonGrouperByCompanyId(companyId).map {
            Account(
                it.accountId,
                it.codeAccount,
                it.nameAccount,
                it.moneyRub,
                it.report,
                it.clasificator
            )
        }
        transactionalVoucherDto.accountablePlan = accountList

        //Obtener cuentas auxiliares
        val auxiliaryList = auxiliaryAccountBl.getAuxiliariesAccountByCompanyId(companyId).map {
            Auxiliary(
                it.auxiliaryAccountId,
                it.codeAccount,
                it.nameDescription
            )
        }
        transactionalVoucherDto.auxiliar = auxiliaryList

        return transactionalVoucherDto
    }

    fun factoryTransaction(transactionDto: TransactionDto, companyId: Int, userId: Int): TransactionEntity{
        val transactionEntity = TransactionEntity()
        transactionEntity.transactionTypeId = transactionDto.transactionTypeId
        transactionEntity.transactionNumber= (transactionDao.getLastTransactionNumber(companyId)?.plus(1)?:1).toLong()
        //transactionEntity.transactionNumber= transactionDto.transactionNumber
        transactionEntity.glosaGeneral = transactionDto.glosaGeneral
        transactionEntity.date= LocalDateTime.now()
        transactionEntity.exchangeRateId = transactionDto.currencyId
        transactionEntity.areaSubsidiaryId = areaSubsidiaryDao.findAreaSubsidiaryId(transactionDto.subsidiaryId, transactionDto.areaId)
        transactionEntity.companyId = companyId
        transactionEntity.userId = userId
        return transactionEntity
    }

    fun factoryTransactionAccount(transactionAccountDto: TransactionAccountDto): TransactionAccountEntity{
        val transactionAccountEntity = TransactionAccountEntity()
        transactionAccountEntity.entityId = transactionAccountDto.entityId
        transactionAccountEntity.accountId = transactionAccountDto.accountId
        transactionAccountEntity.auxiliaryAccountId = transactionAccountDto.auxiliaryId
        transactionAccountEntity.glosaDetail = transactionAccountDto.glosaDetail
        transactionAccountEntity.documentNumber = transactionAccountDto.documentCode
        transactionAccountEntity.dueDate = transactionAccountDto.emitedDate
        return transactionAccountEntity
    }

    fun factoryCreditDebit(transactionAccountDto: TransactionAccountDto, transactionId: Long, exchangeRateId: Int): DebitCreditEntity{
        val debitCreditEntity = DebitCreditEntity()
        debitCreditEntity.transactionAccountId = transactionId
        debitCreditEntity.amountDebit = transactionAccountDto.amountDebit
        debitCreditEntity.amountCredit = transactionAccountDto.amountCredit
        debitCreditEntity.exchangeRateId = exchangeRateId
        return debitCreditEntity
    }





}