package com.ucb.edu.abc.mscompany.dao


import com.ucb.edu.abc.mscompany.entity.TransactionTypeEntity
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component

@Mapper
@Component
interface TransactionTypeDao {

    @Select("SELECT * FROM transaction_type")
    fun getAllTransactionType(): List<TransactionTypeEntity>
}