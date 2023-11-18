package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.dto.response.AccountDto
import com.ucb.edu.abc.mscompany.dto.response.TransactionLedger
import com.ucb.edu.abc.mscompany.entity.TransactionEntity
import com.ucb.edu.abc.mscompany.entity.pojos.TransactionViewPojo
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component
import java.util.Date

@Mapper
@Component
interface TransactionDao {
    @Options(useGeneratedKeys = true, keyProperty = "transactionId")
    @Insert(
            """
    INSERT INTO transaction ( transaction_type_id, transaction_number, glosa_general, date, exchange_rate_id, area_subsidiary_id, company_id,user_id,ajuste)
    VALUES ( #{transactionTypeId}, #{transactionNumber}, #{glosaGeneral}, #{date}, #{exchangeRateId}, #{areaSubsidiaryId}, #{companyId}, #{userId}, #{ajuste})
    """
    )

    fun create(transaction: TransactionEntity)

    @Select("SELECT MAX(transaction_number) FROM transaction WHERE company_id = #{companyId}")
    fun getLastTransactionNumber(companyId: Int): Int?

    @Select(
            """
    SELECT t.transaction_id, t.transaction_type_id ,t.transaction_number, t.glosa_general, t.date, t.exchange_rate_id
    FROM transaction t
    JOIN area_subsidiary a_s ON t.area_subsidiary_id = a_s.area_subsidiary_id
    WHERE a_s.subsidiary_id = #{subsidiaryId} AND a_s.area_id = #{areaId} AND t.transaction_type_id = #{transactionTypeId} AND t.company_id = #{companyId} AND t.date BETWEEN #{from} AND #{to}
    """
    )

    fun getTransactionForAreaAndSubsidiary(companyId: Int, subsidiaryId: Int, areaId: Int, from: Date, to: Date, transactionTypeId: Int ): List<TransactionEntity>

  /*  @Select(
            """
    SELECT 
        a.code_account,
        a.name_account,
        ta.glosa_detail,
        SUM(dc.amount_debit) AS debitamount,
        SUM(dc.amount_credit) AS creditamount
    FROM 
        transaction t
    JOIN 
        transaction_account ta ON t.transaction_id = ta.transaction_id
    JOIN 
        account a ON ta.account_id = a.account_id
    LEFT JOIN
        debit_credit dc ON ta.transaction_account_id = dc.transaction_account_id
    WHERE 
        t.transaction_id = #{transactionId}
    AND 
        dc.exchange_rate_id = #{exchangeRateId}
    GROUP BY 
        a.code_account, 
        a.name_account, 
        ta.glosa_detail
    """
    )
    fun getAccountDetailsByTransactionId(transactionId: Long, exchangeRateId: Int): List<AccountDto>
*/
  @Select(
          """
    SELECT 
        a.code_account,
        a.name_account,
        ta.glosa_detail,
        dc.amount_debit,
        dc.amount_credit
    FROM 
        transaction t
    JOIN 
        transaction_account ta ON t.transaction_id = ta.transaction_id
    JOIN 
        account a ON ta.account_id = a.account_id
    JOIN 
        debit_credit dc ON ta.transaction_account_id = dc.transaction_account_id
    JOIN
        exchange_rate er ON t.exchange_rate_id = er.exchange_rate_id
    WHERE 
        t.transaction_id = #{transactionId}
    AND 
        er.abbreviation_name = #{exchangeMoneyIso}
    AND 
        dc.exchange_rate_id = er.exchange_rate_id
    """
  )
  fun getAccountDetailsByTransactionId(transactionId: Long, exchangeMoneyIso: String): List<AccountDto>

    @Select("SELECT t.transaction_id, t.transaction_number, t.exchange_rate_id, t.date, t.glosa_general" +
            " FROM transaction t" +
            " INNER JOIN area_subsidiary asub ON t.area_subsidiary_id = asub.area_subsidiary_id" +
            " INNER JOIN area a ON asub.area_id = a.area_id" +
            " INNER JOIN subsidiary s ON asub.subsidiary_id = s.subsidiary_id" +
            " WHERE t.company_id = #{companyId}" +
            " AND a.area_id = #{areaId}" +
            " AND s.subsidiary_id = #{subsidiaryId}" +
            " AND t.transaction_type_id = #{transactionTypeId}")
    fun getListTransactions(companyId: Int, subsidiaryId: Int, areaId: Int, transactionTypeId: Int): List<TransactionViewPojo>


     //   GET TRANSACTION fOR LEDGER
    @Select(
            """
                SELECT 
                    t.transaction_number,
                    t.date,
                    tt.type,
                    ta.glosa_detail,
                    ta.document_number,
                    dc.amount_debit,
                    dc.amount_credit,
                    dc.amount_debit - dc.amount_credit AS balances
                FROM
                    transaction t
                JOIN transaction_account ta ON t.transaction_id = ta.transaction_id
                JOIN debit_credit dc ON ta.transaction_account_id = dc.transaction_account_id
                JOIN transaction_type tt ON t.transaction_type_id = tt.transaction_type_id
                JOIN exchange_rate er on t.exchange_rate_id = er.exchange_rate_id
                WHERE
                    ta.account_id = #{accountId}
                AND	
                    t.date BETWEEN #{from} AND #{to}
                AND 
                    t.area_subsidiary_id= #{areaSubsidiaryId}
                AND 
                    dc.exchange_rate_id= er.exchange_rate_id
                AND 
                    ta.company_id= #{companyId}
                AND
                    er.abbreviation_name = #{exchangeRateIso}
                                
          """
    )
    fun getLedgerTransactions(companyId: Int, accountId: Int, areaSubsidiaryId: Int?, from: Date, to: Date, exchangeRateIso: String): List<TransactionLedger>

    @Select("SELECT * FROM transaction WHERE transaction_id = #{transactionId}")
    fun getTransactionById(transactionId: Int): TransactionEntity?

}