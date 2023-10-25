package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.dto.response.TransactionListDto
import com.ucb.edu.abc.mscompany.entity.TransactionAccountEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component

@Mapper
@Component
interface TransactionAccountDao {

    @Options(useGeneratedKeys = true, keyProperty = "transactionAccountId")
    @Insert(
            """
                INSERT INTO transaction_account ( entity_id, transaction_id, account_id, auxiliary_account_id, 
                glosa_detail, document_number, company_id)
                VALUES ( #{entityId}, #{transactionId}, #{accountId}, #{auxiliaryAccountId}, #{glosaDetail}, #{documentNumber}, #{companyId})
         """
    )
    fun create(transactionAccountEntity: TransactionAccountEntity)


    @Select("SELECT a.account_id, a.code_account, e.entity_id, e.entity_name," +
            " aux.auxiliary_account_id as auxiliaryId, aux.code_account, " +
            " dc.amount_debit, dc.amount_credit, ta.glosa_detail, ta.document_number" +
            " FROM account a, entity e, auxiliary_account aux, transaction_account ta, debit_credit dc" +
            " WHERE ta.transaction_account_id = #{transactionAccountId} AND " +
            " ta.entity_id = e.entity_id AND ta.account_id = a.account_id AND " +
            " ta.auxiliary_account_id = aux.auxiliary_account_id AND " +
            " ta.transaction_account_id = dc.transaction_account_id")
    fun getTransactionAccount(transactionAccountId: Int): List<TransactionListDto>



}