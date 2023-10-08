package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.AreaSubsidiaryDao
import com.ucb.edu.abc.mscompany.dao.DebitCreditDao
import com.ucb.edu.abc.mscompany.dao.TransactionAccountDao
import com.ucb.edu.abc.mscompany.dao.TransactionDao
import com.ucb.edu.abc.mscompany.dto.request.TransactionAccountDto
import com.ucb.edu.abc.mscompany.dto.request.TransactionDto
import com.ucb.edu.abc.mscompany.dto.response.*
import com.ucb.edu.abc.mscompany.entity.DebitCreditEntity
import com.ucb.edu.abc.mscompany.entity.TransactionAccountEntity
import com.ucb.edu.abc.mscompany.entity.TransactionEntity
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
        private val subsidiaryBl: SubsidiaryBl,
        private val areaBl: AreaBl,
        private val exchangeMoneyBl: ExchangeMoneyBl,
        private val accountBl: AccountBl,
        private val auxiliaryAccountBl: AuxiliaryAccountBl,
        private val companyBl: CompanyBl,


){
    private val logger: Logger = LoggerFactory.getLogger(CompanyBl::class.java)

    fun saveTransaction(companyId: Int, transactionDto: TransactionDto){
        logger.info("Guardando transaccion")
        if (transactionDto.totalCredit != transactionDto.totalDebit){
            throw Exception("El total de creditos y debitos no coinciden")
        }
        val transactionEntity = factoryTransaction(transactionDto)
        transactionDao.create(transactionEntity)

        //registrar en transaction Account
        transactionDto.transactions.forEach {
            val transactionAccountEntity = factoryTransactionAccount(it)
            transactionAccountEntity.transactionId = transactionEntity.transactionId
            transactionAccountEntity.userId = transactionDto.userId
            transactionAccountEntity.companyId = companyId

            //OJO -->Tal vez seria bueno cambiar la bd, ya que el userid está fuera de la lista, en la bd
            //en vez de poner el userid en transactionAccount, tal vez ponerlo simplemente en transaction, así no repetiriamos el userid en toda la transaccion
            //esto porque de todas maneras al estar el userId fuera, igual se repetirá

            transactionAccountDao.create(transactionAccountEntity)
            val debitCreditEntity = factoryCreditDebit(it, transactionAccountEntity.transactionAccountId, transactionDto.currencyId)
            debitCreditDao.create(debitCreditEntity)
        }
    }


    fun getDataForDoATransaction(companyId: Int, user: String): TransactionalVoucherDto{
        val transactionalVoucherDto = TransactionalVoucherDto()

        //Obteniendo los datos sueltos del response
        transactionalVoucherDto.transactionNumber = (transactionDao.getLastTransactionNumber(companyId)?:0) + 1
        transactionalVoucherDto.companyName = companyBl.getCompanyName(companyId)

        //Obteniendo la lista de transactionType
        val transactionTypeList = transactionTypeBl.getTransactionTypeByCompanyId(companyId)
        transactionalVoucherDto.transactionType = transactionTypeList

        //Obtener Subsidiaries
        //TODO: verificar que tiene acceso a las subsidiarias, verificar que es editable
        val subsidiaryList = subsidiaryBl.getByCompanyId(companyId).map{
            Subsidiary(it.subsidiaryId, it.subsidiaryName, it.address?:"", false)
        }
        transactionalVoucherDto.subsidiaries = subsidiaryList

        //Obtener Areas
        //TODO: verificar que tiene acceso a las areas, verificar que es editable
        val areaList = areaBl.getAreasByCompanyId(companyId).map {
            Area(it.areaId, it.areaName, false)
        }
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

    fun factoryTransaction(transactionDto: TransactionDto): TransactionEntity{
        val transactionEntity = TransactionEntity()
        transactionEntity.transactionTypeId = transactionDto.transactionTypeId
        transactionEntity.transactionNumber= transactionDto.transactionNumber
        transactionEntity.glosaGeneral = transactionDto.glosaGeneral
        transactionEntity.date= LocalDateTime.now()
        transactionEntity.exchangeRateId = transactionDto.currencyId
        transactionEntity.areaSubsidiaryId =   areaSubsidiaryDao.findAreaSubsidiaryId(transactionDto.subsidiaryId, transactionDto.areaId)
        transactionEntity.companyId = transactionDto.companyId
        return transactionEntity
    }

    fun factoryTransactionAccount(transactionAccountDto: TransactionAccountDto): TransactionAccountEntity{
        val transactionAccountEntity = TransactionAccountEntity()
        transactionAccountEntity.entityId = transactionAccountDto.entityId
        transactionAccountEntity.accountId = transactionAccountDto.accountCode
        transactionAccountEntity.auxiliaryAccountId = transactionAccountDto.auxiliaryId
        transactionAccountEntity.glosaDetail = transactionAccountDto.glosaDetail
        transactionAccountEntity.documentNumber = transactionAccountDto.documentCode
        // en el mock falta el due date (creo xD)
        transactionAccountEntity.dueDate = Date()
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