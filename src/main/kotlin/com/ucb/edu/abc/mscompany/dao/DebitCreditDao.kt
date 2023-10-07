package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.DebitCreditEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component

@Mapper
@Component
interface DebitCreditDao {
    @Options(useGeneratedKeys = true, keyProperty = "debitCreditId")
    @Insert(
            """
                INSERT INTO debit_credit (transaction_account_id, amount_credit, amount_debit, exchange_rate_id)
                VALUES ( #{transactionAccountId}, #{amountCredit}, #{amountDebit}, #{exchangeRateId})
         """
    )
    fun create(debitCredit: DebitCreditEntity)


}