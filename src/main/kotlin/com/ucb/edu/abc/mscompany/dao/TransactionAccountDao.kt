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


    @Select("SELECT a.account_id, a.code_account, a.name_account, e.entity_id, e.entity_name, " +
            "     aux.auxiliary_account_id as auxiliaryId, aux.code_account, " +
            "     dc.amount_debit, dc.amount_credit, ta.glosa_detail, ta.document_number as documentCode " +
            "FROM transaction_account ta " +
            "         LEFT JOIN account a ON ta.account_id = a.account_id " +
            "         LEFT JOIN entity e ON ta.entity_id = e.entity_id " +
            "         LEFT JOIN auxiliary_account aux ON ta.auxiliary_account_id = aux.auxiliary_account_id " +
            "         LEFT JOIN debit_credit dc ON ta.transaction_account_id = dc.transaction_account_id " +
            "WHERE ta.transaction_id = #{transactionAccountId};")
    fun getTransactionAccount(transactionAccountId: Int): List<TransactionListDto>



}