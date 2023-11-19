package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.config.FormatDataClass
import com.ucb.edu.abc.mscompany.dao.*
import com.ucb.edu.abc.mscompany.dto.request.TransactionAccountDto
import com.ucb.edu.abc.mscompany.dto.request.TransactionDto
import com.ucb.edu.abc.mscompany.dto.response.*
import com.ucb.edu.abc.mscompany.entity.DebitCreditEntity
import com.ucb.edu.abc.mscompany.entity.TransactionAccountEntity
import com.ucb.edu.abc.mscompany.entity.TransactionEntity
import com.ucb.edu.abc.mscompany.enums.UserAbcCategory
import com.ucb.edu.abc.mscompany.exception.EmptyResponseException
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class TransactionBl @Autowired constructor(
        private val transactionDao: TransactionDao,
        private val transactionAccountDao: TransactionAccountDao,
        private val exchangeRateDao: ExchangeRateDao,
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
        private val companyDao: CompanyDao,
        private val exchangeRateBl: ExchangeRateBl,
        private val entityBl: EntityBl,
        private val transactionAccountBl: TransactionAccountBl,
        private val areaSubsidiaryBl: AreaSubsidiaryBl,
        private val format: FormatDataClass


){
    private val logger: Logger = LoggerFactory.getLogger(CompanyBl::class.java)

    fun saveTransaction(companyId: Int, transactionDto: TransactionDto, headers: Map<String, String>){

        val exchange = exchangeRateDao.getExchangeByCompanyIdAndAbbreviationName(
                companyId,
                transactionDto.currencyId,
                transactionDto.date)

        val exchangeList = exchangeRateDao.getExchangeList(companyId, transactionDto.date) // Pasar LocalDate

        val tokenAuth =  headers["authorization"]!!.substring(7)
        val userId = userBl.getUserIdByCompanyIdAndToken (tokenAuth,   companyId, UserAbcCategory.ACTIVE,null)

        logger.info("Guardando transaccion")
        if (transactionDto.totalCredit != transactionDto.totalDebit){
            throw Exception("El total de creditos y debitos no coinciden")
        }


        if(transactionDto.transactionTypeId == 4){
            logger.info("Abriendo contabilidad de la empresa $companyId")
            companyDao.updateStatusCompany(companyId,true)
        }


        val transactionEntity = factoryTransaction(transactionDto, companyId, userId, exchange.exchangeRateId)
        transactionDao.create(transactionEntity)

        //registrar en transaction Account

        logger.info("Registrando transaccion sin ajuste")
        transactionDto.transactions.forEach {
            val transactionAccountEntity = factoryTransactionAccount(it)
            transactionAccountEntity.transactionId = transactionEntity.transactionId
            transactionAccountEntity.companyId = companyId
            transactionAccountDao.create(transactionAccountEntity)
            if(!transactionDto.ajuste){
                val debitCreditEntity = factoryCreditDebit(it, transactionAccountEntity.transactionAccountId.toLong(), exchange.exchangeRateId)
                debitCreditDao.create(debitCreditEntity)
            }else {
                val isTransactionInBob = transactionDto.currencyId.equals("BOL", ignoreCase = true)

                val amountInBobDebit: BigDecimal
                val amountInBobCredit: BigDecimal

                if (isTransactionInBob) {
                    amountInBobDebit = it.amountDebit
                    amountInBobCredit = it.amountCredit
                } else {
                    val amountDebit = it.amountDebit.toDouble()
                    val amountCredit = it.amountCredit.toDouble()
                    val transactionCurrencyToBobRate = exchange.currency.toDouble()
                    amountInBobDebit = (amountDebit * transactionCurrencyToBobRate).toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                    amountInBobCredit = (amountCredit * transactionCurrencyToBobRate).toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                }


                for (otherExchange in exchangeList) {
                    if (otherExchange.exchangeRateId != exchange.exchangeRateId) {
                        val debitCreditEntity = DebitCreditEntity()
                        debitCreditEntity.transactionAccountId = transactionAccountEntity.transactionAccountId.toLong()
                        debitCreditEntity.exchangeRateId = otherExchange.exchangeRateId

                        val amountDebit = amountInBobDebit.toDouble()
                        val amountCredit = amountInBobCredit.toDouble()
                        val otherExchangeRate = otherExchange.currency.toDouble()
                        logger.info("******")
                        logger.info("amountDebit: $amountDebit")
                        logger.info("amountCredit: $amountCredit")
                        logger.info("otherExchangeRate: $otherExchangeRate")
                        logger.info("******")
                        debitCreditEntity.amountDebit = (amountDebit / otherExchangeRate).toBigDecimal()
                        debitCreditEntity.amountCredit = (amountCredit / otherExchangeRate).toBigDecimal()

                        debitCreditDao.create(debitCreditEntity)
                    }else{
                        val debitCreditEntity = DebitCreditEntity()
                        debitCreditEntity.transactionAccountId = transactionAccountEntity.transactionAccountId.toLong()
                        debitCreditEntity.exchangeRateId = otherExchange.exchangeRateId
                        debitCreditEntity.amountDebit = amountInBobDebit
                        debitCreditEntity.amountCredit = amountInBobCredit
                        debitCreditDao.create(debitCreditEntity)
                    }
                }

            }

        }


    }


    fun getDataForDoATransaction(companyId: Int, headers: Map<String,String>): TransactionalVoucherDto{
        val transactionalVoucherDto = TransactionalVoucherDto()
        val tokenAuth =  headers["authorization"]!!.substring(7)
        val userId = userBl.getUserIdByCompanyIdAndToken (tokenAuth,   companyId, UserAbcCategory.ACTIVE,null)

        //Obteniendo los datos sueltos del response
        transactionalVoucherDto.transactionNumber = (transactionDao.getLastTransactionNumber(companyId)?:0) + 1
        transactionalVoucherDto.companyName = companyBl.getCompanyName(companyId)


        logger.info("Verificando el cierre de contabilidad de la empresa $companyId")

        transactionalVoucherDto.isOpen = companyDao.getStatusCompany(companyId)

        val lastClosingDate = getLastClosingSheet(companyId)


        if (lastClosingDate != null) {
            transactionalVoucherDto.lastClosing= lastClosingDate
        }

        if (!transactionalVoucherDto.isOpen) {logger.info("La contabilidad de la empresa $companyId esta cerrada")
        } else {logger.info("La contabilidad de la empresa $companyId esta abierta")}

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

    fun factoryTransaction(transactionDto: TransactionDto, companyId: Int, userId: Int, exchangeRateId: Int): TransactionEntity{
        val transactionEntity = TransactionEntity()
        transactionEntity.transactionTypeId = transactionDto.transactionTypeId.toLong()
        transactionEntity.transactionNumber= (transactionDao.getLastTransactionNumber(companyId)?.plus(1)?:1).toLong()
        transactionEntity.glosaGeneral = transactionDto.glosaGeneral
        transactionEntity.date = LocalDateTime.now()
        transactionEntity.ajuste= transactionDto.ajuste
        transactionEntity.exchangeRateId = exchangeRateId
        transactionEntity.areaSubsidiaryId = areaSubsidiaryDao.findAreaSubsidiaryId(transactionDto.subsidiaryId, transactionDto.areaId)
        transactionEntity.companyId = companyId
        transactionEntity.userId = userId
        return transactionEntity
    }

    fun factoryTransactionAccount(transactionAccountDto: TransactionAccountDto): TransactionAccountEntity{
        val transactionAccountEntity = TransactionAccountEntity()
        transactionAccountEntity.entityId = transactionAccountDto.entityId
        transactionAccountEntity.accountId = transactionAccountDto.accountId.toLong()
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
                val list = transactionAccountBl.getAllTransactionByTransactionId(it.transactionId, it.exchangeRateId)
                ListTransactionDto(
                        it.transactionNumber,
                        it.transactionId,
                        exchangeRateBl.getExchangeRate(it.exchangeRateId),
                        getStringDate(it.date.time),
                        it.glosaGeneral,
                        list,
                        list.sumOf {tr -> tr.amountDebit },
                        list.sumOf {tr -> tr.amountCredit }
                )
            }
            return listTransaction
        }catch (e: Exception){
            logger.error("Error al obtener las transacciones ", e.message.toString())
            throw PostgresException("Error al obtener las transacciones ", e.message.toString())
        }
    }


    fun getStringDate(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(date)
    }

    fun getStringDateFromLocalDateTime(localDateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return localDateTime.format(formatter)
    }

    fun getLastClosingSheet(companyId: Int): Date{

        return closingSheetDao.getLastClosing(companyId) ?: companyDao.getOpeningDate(companyId)

    }

    fun getTransactionByIdAndCurrency(companyId: Int, transactionId: Int, currency: String): ListTransactionDto {
        val transactionEntity = getTransactionById(transactionId)
        val dateString = getStringDateFromLocalDateTime(transactionEntity.date)
        val exchangeRateEntity = exchangeRateBl.getExchangeRateByDateAndIso(companyId, dateString, currency)
        val listTransaction = transactionAccountBl.getAllTransactionByTransactionIdAndCurrencyIso(transactionId, currency)
        return ListTransactionDto(
                transactionEntity.transactionNumber.toInt(),
                transactionEntity.transactionId.toInt(),
                CurrencyVoucher(
                        exchangeRateEntity.exchangeRateId,
                        exchangeRateEntity.moneyName,
                        exchangeRateEntity.abbreviationName,
                        exchangeRateEntity.currency
                ),
                dateString,
                transactionEntity.glosaGeneral,
                listTransaction,
                listTransaction.sumOf { tr -> tr.amountDebit },
                listTransaction.sumOf { tr -> tr.amountCredit }
        )
    }

    fun getTransactionById(transactionId: Int): TransactionEntity{
        try {
            val transactionEntity = transactionDao.getTransactionById(transactionId)
            if(transactionEntity == null){
                logger.error("No se encontro la transaccion")
                throw EmptyResponseException("No se encontro la transaccion")
            }
            return transactionEntity
        }catch (e: Exception){
            logger.error("Error al obtener la transaccion", e.message.toString())
            throw PostgresException("Error al obtener la transaccion", e.message.toString())
        }
    }

    suspend fun getPDFTransaction(companyId: Int, transactionId: Int, currency: String): ListTransactionDtoPdf{
        val logoUrl = companyBl.getUrlImageByCompanyId(companyId)
        val stringToday = getStringDateFromLocalDateTime(LocalDateTime.now())
        val transactionEntity = getTransactionById(transactionId)
        val areaSubsidiaryData = areaSubsidiaryBl.getAreaAndSubsidiaryNameById(transactionEntity.areaSubsidiaryId!!)
        val dateString = getStringDateFromLocalDateTime(transactionEntity.date)
        val exchangeRateEntity = exchangeRateBl.getExchangeRateByDateAndIso(companyId, dateString, currency)
        val transaction = transactionAccountBl.getAllTransactionByTransactionIdAndCurrencyIso(transactionId, currency)
        val listTransaction = transaction.map {
            TransactionListDtoPdf(
                    it.accountId,
                    it.accountCode,
                    it.nameAccount,
                    it.entityId,
                    it.entityName,
                    it.auxiliaryId,
                    it.codeAccount,
                    format.getNumber(it.amountDebit.toBigDecimal()),
                    format.getNumber(it.amountCredit.toBigDecimal()),
                    it.glosaDetail,
                    it.documentCode
            )
        }
        return ListTransactionDtoPdf(
                logoUrl,
                stringToday,
                areaSubsidiaryData.subsidiaryName,
                areaSubsidiaryData.areaName,
                transactionEntity.transactionNumber.toInt(),
                transactionEntity.transactionId.toInt(),
                CurrencyVoucher(
                        exchangeRateEntity.exchangeRateId,
                        exchangeRateEntity.moneyName,
                        exchangeRateEntity.abbreviationName,
                        exchangeRateEntity.currency
                ),
                dateString,
                transactionEntity.glosaGeneral,
                listTransaction,
                format.getNumber(transaction.sumOf { tr -> tr.amountDebit }.toBigDecimal()),
                format.getNumber(transaction.sumOf { tr -> tr.amountCredit }.toBigDecimal())
        )
    }










}