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
        INSERT INTO report (date, user_id, company_id, uuid)
        VALUES (#{date}, #{userId}, #{companyId}, #{uuid})
        """
    )
    fun insertReport(reportEntity: ReportEntity)
}