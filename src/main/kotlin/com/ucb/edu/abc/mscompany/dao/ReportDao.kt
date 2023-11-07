package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.ReportEntity
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.springframework.stereotype.Component

@Mapper
@Component
interface ReportDao {

    @Insert(
        """
        INSERT INTO report (date, user_id, company_id, uuid, type_document, type_report)
        VALUES (now(), #{userId}, #{companyId}, #{uuid}, #{typeDocument}, #{typeReport})
        """
    )
    fun insertReport(reportEntity: ReportEntity)
}