package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.TransactionAccountEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.springframework.stereotype.Component

@Mapper
@Component
interface TransactionAccountDao {

    @Options(useGeneratedKeys = true, keyProperty = "transactionAccountId")
    @Insert(
            """
                INSERT INTO transaction_account ( entity_id, transaction_id, account_id, auxiliary_account_id, 
                glosa_detail, document_number,due_date,company_id)
                VALUES ( #{entityId}, #{transactionId}, #{accountId}, #{auxiliaryAccountId}, #{glosaDetail}, #{documentNumber}, #{dueDate}, #{companyId})
         """
    )
    fun create(transactionAccountEntity: TransactionAccountEntity)



}