package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.ClosingSheetEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.springframework.stereotype.Component

@Mapper
@Component
interface ClosingSheetDao {
    @Options(useGeneratedKeys = true, keyProperty = "closingSheetId")
    @Insert(
        """
            INSERT INTO closing_sheet (company_id,user_id,description, date) 
            VALUES ( #{companyId},#{userId}, #{description}, #{date})
        """
    )
    fun createClosing(closing: ClosingSheetEntity)

}