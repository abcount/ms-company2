package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.ClosingSheetEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component
import java.util.Date

@Mapper
@Component
interface ClosingSheetDao {
    @Options(useGeneratedKeys = true, keyProperty = "closingSheetId")
    @Insert(
        """
            INSERT INTO closing_sheet (company_id,user_id,description, date) 
            VALUES ( #{companyId},#{userId}, #{description}, now())
        """
    )
    fun createClosing(closing: ClosingSheetEntity)

    // obtener el ultimo closing sheet
    @Select(
        """
            SELECT date
            FROM closing_sheet
            WHERE closing_sheet_id = (
                SELECT MAX(closing_sheet_id)
                FROM closing_sheet
                WHERE company_id = #{companyId}
            );
                        
        """
    )
    fun getLastClosing(companyId: Int) : Date?

}