package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.AccountDao
import com.ucb.edu.abc.mscompany.dto.request.AccountDto
import com.ucb.edu.abc.mscompany.dto.request.AccountablePlanDto
import com.ucb.edu.abc.mscompany.entity.AccountEntity
import com.ucb.edu.abc.mscompany.entity.CompanyEntity
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.apache.ibatis.exceptions.PersistenceException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.SQLException
import java.text.ParseException

@Service
class AccountBl @Autowired constructor(
        private val accountDao: AccountDao
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun create(accountEntity: AccountEntity): Int {
        try {
            logger.info("Creando cuenta")
            accountDao.create(accountEntity)
            return accountEntity.accountId
        } catch (e: Exception) {
            throw PostgresException("Ocurrio un error al crear la cuenta ${accountEntity.toString()}", e.message.toString())
        }
    }

    fun createAccountPlan(accounts: List<AccountDto>, companyId: Int) {
        for(account in accounts) {
            val dad = create(factoryAccount(companyId, account))
            if(account.childrenAccounts.isNotEmpty()) {
                createChildrenOfAccount(account.childrenAccounts, dad, companyId)
            }
        }
    }

    fun createChildrenOfAccount(accounts: List<AccountDto>, dad: Int, companyId: Int){
        for(account in accounts){
            val child = createChildOfAccount(factoryAccount( companyId,account), dad)
            if(account.childrenAccounts.isNotEmpty()) {
                createChildrenOfAccount(account.childrenAccounts, child, companyId)
            }
        }
    }

    fun createChildOfAccount(account: AccountEntity, dad: Int): Int {
        account.accountAccountId = dad
        return create(account)
    }

    fun factoryAccount(companyId: Int, accountDto: AccountDto): AccountEntity{
        val accountEntity = AccountEntity()
        accountEntity.companyId = companyId
        accountEntity.codeAccount = accountDto.accountCode
        accountEntity.nameAccount = accountDto.nameAccount
        accountEntity.clasificator = accountDto.classificator
        accountEntity.level = accountDto.level
        accountEntity.report = accountDto.report
        accountEntity.status = true
        accountEntity.moneyRub = accountDto.moneyRub
        accountEntity.accountAccountId = null
        return accountEntity
    }



    fun getAccountPlan(companyId: Int) : List<AccountablePlanDto> {
        try {
            var accountPlan: MutableList<AccountEntity> = accountDao.getAccountPlanByCompanyId(companyId)
            println(accountPlan)
            return organizeAccountablePlan(accountPlan, mutableListOf<AccountablePlanDto>())

        } catch (e: PersistenceException) {
            throw PostgresException("Ocurrio un error al actualizar la compañia ", e.message.toString())
        }
    }

    fun organizeAccountablePlan(accountPlan: MutableList<AccountEntity>, accountablePlanDto: MutableList<AccountablePlanDto>): List<AccountablePlanDto> {
        for (i in 0 until accountPlan.size ){
            if (accountPlan[i].level == 0){
                accountablePlanDto.add(setAccountIntoAccountablePlanDto(accountPlan[i], AccountablePlanDto()))
            }
        }
        var accountablePlanDto = accountablePlanTree(accountPlan, accountablePlanDto) as MutableList<AccountablePlanDto>
      return accountablePlanDto
    }

    fun accountablePlanTree(accountPlan: MutableList<AccountEntity>, accountablePlanDto: MutableList<AccountablePlanDto>): List<AccountablePlanDto> {
        for (i in 0 until accountablePlanDto.size ){
            for (j in 0 until accountPlan.size ){
                if (accountPlan[j].accountAccountId == accountablePlanDto[i].accountId){
                    accountablePlanDto[i].childrenAccounts.add(setAccountIntoAccountablePlanDto(accountPlan[j], AccountablePlanDto()))
                }
            }
            if (accountablePlanDto[i].childrenAccounts.isNotEmpty()){
                accountablePlanTree(accountPlan, accountablePlanDto[i].childrenAccounts)
            }
        }
      return accountablePlanDto
    }

    fun setAccountIntoAccountablePlanDto(account: AccountEntity, accountablePlanDto: AccountablePlanDto): AccountablePlanDto {
        accountablePlanDto.accountId = account.accountId
        accountablePlanDto.accountCode = account.codeAccount
        accountablePlanDto.nameAccount = account.nameAccount
        accountablePlanDto.moneyRub = account.moneyRub
        accountablePlanDto.report = account.report
        accountablePlanDto.clasificator = account.clasificator
        accountablePlanDto.level = account.level
        accountablePlanDto.editable = true //TODO need to develop a function that verifies if the account is editable or not
        return accountablePlanDto
    }


}