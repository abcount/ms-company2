package com.ucb.edu.abc.mscompany.bl
import com.ucb.edu.abc.mscompany.dao.*
import com.ucb.edu.abc.mscompany.dto.request.JournalRequestDto
import com.ucb.edu.abc.mscompany.dto.request.LedgerRequestDto
import com.ucb.edu.abc.mscompany.dto.response.*
import com.ucb.edu.abc.mscompany.entity.TransactionEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class LedgerBl @Autowired constructor(
        private val transactionDao: TransactionDao,
        private val companyDao: CompanyDao
){
    private val logger: Logger = LoggerFactory.getLogger(CompanyBl::class.java)
/*
    fun getLedger(companyId: Int, ledgerRequestDto: LedgerRequestDto): LedgerResponseDto {
        logger.info("Obteniendo libro mayor")
        val company = companyDao.getCompanyById(companyId)
        val accountDtoList = mutableListOf<AccountDto>()



        return LedgerResponseDto(company.companyName, ledgerRequestDto.from, ledgerRequestDto.to,"Bolivianos",accountDtoList)
    }
*/
}