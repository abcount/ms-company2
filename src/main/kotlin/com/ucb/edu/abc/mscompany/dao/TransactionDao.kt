package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.TransactionEntity
import com.ucb.edu.abc.mscompany.entity.aux.CompanyDataVoucher
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component

@Mapper
@Component
interface TransactionDao {
    @Options(useGeneratedKeys = true, keyProperty = "transactionId")
    @Insert(
            """
    INSERT INTO transaction ( transaction_type_id, transaction_number, glosa_general, date, exchange_rate_id, area_subsidiary_id, company_id)
    VALUES ( #{transactionTypeId}, #{transactionNumber}, #{glosaGeneral}, #{date}, #{exchangeRateId}, #{areaSubsidiaryId}, #{companyId})
    """
    )

    fun create(transaction: TransactionEntity)

    @Select("SELECT MAX(transaction_number) FROM transaction WHERE company_id = #{companyId}")
    fun getLastTransactionNumber(companyId: Int): Int?

}