package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.dto.response.TransactionType
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component

@Mapper
@Component
interface TransactionTypeDao {

    @Select("SELECT transaction_type_id, type, description FROM transaction_type " +
            "WHERE company_id = #{companyId}")
    fun getTransactionTypeByCompanyId(companyId: Int): List<TransactionType>
}