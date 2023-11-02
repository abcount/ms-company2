package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.*
import com.ucb.edu.abc.mscompany.dto.request.BalanceGeneralRequestDto
import com.ucb.edu.abc.mscompany.dto.request.JournalRequestDto
import com.ucb.edu.abc.mscompany.dto.response.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BalanceGeneralBl @Autowired constructor(
        private val transactionDao: TransactionDao,
        private val transactionTypeDao: TransactionTypeDao,
        private val subsidiaryDao: SubsidiaryDao,
        private val companyDao: CompanyDao,
        private val areaDao: AreaDao,
        private val exchangeRateDao: ExchangeRateDao
) {
/*
    private val logger: Logger = LoggerFactory.getLogger(CompanyBl::class.java)
    fun getBalanceGeneral(companyId: Int, balanceGeneralRequestDto: BalanceGeneralRequestDto): BalanceGeneralResponseDto {
        logger.info("Obteniendo balance general de la empresa: $companyId")
        val companyEntity = companyDao.getCompanyById(companyId)
        val exchangeRate = exchangeRateDao.getExchangeRateById(balanceGeneralRequestDto.currencies)
        val subsidiaryBalanceDtoList= mutableListOf<SubsidiaryBalance>()
        for (subsidiaryId in balanceGeneralRequestDto.subsidiaries) {
            val subsidiaryEntity = subsidiaryDao.getSubsidiaryById(subsidiaryId)
            var areaBalanceDtoList = mutableListOf<AreaBalance>()
            for (areaId in balanceGeneralRequestDto.areas) {
                var accountBalanceDtoList = mutableListOf<AccountBalance>()

                val areaEntity = areaDao.getAreaById(areaId)
            }

            subsidiaryBalanceDtoList.add(SubsidiaryBalance(subsidiaryEntity.subsidiaryId, subsidiaryEntity.subsidiaryName, areaBalanceDtoList)

        }

        return BalanceGeneralResponseDto()
    }
*/
}