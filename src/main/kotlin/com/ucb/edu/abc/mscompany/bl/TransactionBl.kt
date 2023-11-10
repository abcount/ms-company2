package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.*
import com.ucb.edu.abc.mscompany.dto.request.TransactionAccountDto
import com.ucb.edu.abc.mscompany.dto.request.TransactionDto
import com.ucb.edu.abc.mscompany.dto.response.*
import com.ucb.edu.abc.mscompany.entity.ClosingSheetEntity
import com.ucb.edu.abc.mscompany.entity.DebitCreditEntity
import com.ucb.edu.abc.mscompany.entity.TransactionAccountEntity
import com.ucb.edu.abc.mscompany.entity.TransactionEntity
import com.ucb.edu.abc.mscompany.enums.UserAbcCategory
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

@Service
class TransactionBl @Autowired constructor(
        private val transactionDao: TransactionDao,
        private val transactionAccountDao: TransactionAccountDao,
        private val debitCreditDao: DebitCreditDao,
        private val areaSubsidiaryDao: AreaSubsidiaryDao,
        private val closingSheetDao: ClosingSheetDao,
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
        private val exchangeRateBl: ExchangeRateBl,
        private val entityBl: EntityBl,
        private val transactionAccountBl: TransactionAccountBl


        ){
    private val logger: Logger = LoggerFactory.getLogger(CompanyBl::class.java)

    fun saveTransaction(companyId: Int, transactionDto: TransactionDto, headers: Map<String, String>){
        val tokenAuth =  headers["authorization"]!!.substring(7)
        val userId = userBl.getUserIdByCompanyIdAndToken (tokenAuth,   companyId, UserAbcCategory.ACTIVE,null)

        logger.info("Guardando transaccion")
        if (transactionDto.totalCredit != transactionDto.totalDebit){
            throw Exception("El total de creditos y debitos no coinciden")
        }

        /**
         *
         * CIERRE DE GESTION
         *
         **/
        if( transactionDto.transactionTypeId == 4){
            val closingSheetEntity = convertClosingSheetEntity(companyId,userId)
            closingSheetDao.createClosing(closingSheetEntity)
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
        val exchangeList = exchangeRateBl.getAllExchangeRateByCompanyIdAndDate(companyId).map {
            CurrencyVoucher(
                    it.exchangeRateId,
                    it.moneyName,
                    it.abbreviationName,
                    it.currency
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

        //Obteniendo entidades
        val entityList = entityBl.getAllEntitiesByCompanyId(companyId).map {
            EntityForTransaction(
                    it.entityId,
                    it.entityName,
                    it.nit,
                    it.socialReason,
                    it.foreign
            )
        }
        transactionalVoucherDto.entities = entityList

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

    fun getListTransaction(companyId: Int, subsidiaryId: Int, areaId: Int, transactionTypeId: Int): List<ListTransactionDto>{
        try{
            val listTransaction = transactionDao.getListTransactions(companyId, subsidiaryId, areaId, transactionTypeId).map{
                val list = transactionAccountBl.getAllTransactionByTransactionId(it.transactionId)
                println(list)
                val total = getTotalDebitCredit(list)
                ListTransactionDto(
                    it.transactionNumber,
                    exchangeRateBl.getExchangeRate(it.exchangeRateId!!),
                    getStringDate(it.date.time),
                    it.glosaGeneral,
                    list,
                    total[0],
                    total[1]
                )
            }
            return listTransaction
        }catch (e: Exception){
            logger.error("Error al obtener las transacciones", e.message.toString())
            throw PostgresException("Error al obtener las transacciones ", e.message.toString())
        }
    }


    fun getStringDate(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(date)
    }

    fun getTotalDebitCredit(list: List<TransactionListDto>): List<Double>{
        var totalDebit = 0.0
        var totalCredit = 0.0
        list.forEach{
            totalDebit += it.amountDebit
            totalCredit += it.amountCredit
        }
        return listOf(totalDebit, totalCredit)
    }

    fun convertClosingSheetEntity(companyId: Int, userId: Int) : ClosingSheetEntity{
        val closingSheetEntity = ClosingSheetEntity()
        val date= LocalDateTime.now()
        closingSheetEntity.companyId=companyId
        closingSheetEntity.userId=userId
        closingSheetEntity.description= "Cierre de contabilidad de la empresa $companyId en fecha $date"
        closingSheetEntity.date=date
        closingSheetEntity.status=true

        return closingSheetEntity
    }






}
