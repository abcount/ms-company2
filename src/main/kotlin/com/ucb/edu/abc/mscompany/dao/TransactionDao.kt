package com.ucb.edu.abc.mscompany.dao

import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.springframework.stereotype.Component

@Mapper
@Component
interface TransactionDao {
    //@Options(useGeneratedKeys = true, keyProperty = "companyId")
    //@Insert()
}