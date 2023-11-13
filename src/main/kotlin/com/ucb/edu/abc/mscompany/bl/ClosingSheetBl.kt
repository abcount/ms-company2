package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.ClosingSheetDao
import com.ucb.edu.abc.mscompany.dao.CompanyDao
import com.ucb.edu.abc.mscompany.entity.ClosingSheetEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ClosingSheetBl @Autowired constructor(
        private val closingSheetDao: ClosingSheetDao,
        private val companyDao: CompanyDao
) {
    private val logger: Logger = LoggerFactory.getLogger(CompanyBl::class.java)

    fun closeTransactions(companyId: Int, userId:Int){
        logger.info("Cerrando contabilidad de la empresa $companyId")
        val closingSheetEntity = convertClosingSheetEntity(companyId,userId)
        closingSheetDao.createClosing(closingSheetEntity)
        companyDao.updateStatusCompany(companyId,false)
    }

    fun convertClosingSheetEntity(companyId: Int, userId: Int) : ClosingSheetEntity {
        val closingSheetEntity = ClosingSheetEntity()
        val date= LocalDateTime.now()
        closingSheetEntity.companyId=companyId
        closingSheetEntity.userId=userId
        closingSheetEntity.description= "Cierre empresa $companyId"
        closingSheetEntity.date=date

        return closingSheetEntity
    }
}