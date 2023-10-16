package com.ucb.edu.abc.mscompany.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class SetNewAccountPlan(
    val delete: List<Int>,
    val new: List<NewAccount>
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class NewAccount(
    val accountCode: String,
    val nameAccount: String,
    val moneyRub: Boolean,
    val report: Boolean,
    val clasificator: Boolean,
    val level: Int,
    val dad: Int?
)
