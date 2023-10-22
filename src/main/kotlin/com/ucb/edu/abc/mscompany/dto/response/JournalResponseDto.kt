package com.ucb.edu.abc.mscompany.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.math.BigDecimal
import java.time.LocalDateTime

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class JournalResponseDto (
        var subsidiaries: MutableList<SubsidiaryDto>
){}
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class SubsidiaryDto(
        val subsidiaryId: Int,
        val subsidiaryName: String,
        val areas: List<AreaDto>
){}

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class AreaDto(
        val areaId: Int,
        val areaName: String,
        val transactions: List<TransactionDto>
){}
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class TransactionDto(
        val transactionNumber: Long,
        val transactionType: String?,
        val registrationDate: LocalDateTime,
        val exchangeRate: BigDecimal,
        val glosaGeneral: String,
        val accounts: List<AccountDto>
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class AccountDto(
        val codeAccount: String,
        val nameAccount: String,
        val glosaDetail: String,
        val debitAmount: BigDecimal,
        val creditAmount: BigDecimal
)