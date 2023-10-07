package com.ucb.edu.abc.mscompany.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class CreateCompanyDto(
        var enterprise: CompanyDto,
        var currencyConfig: CurrencyConfigDto,
        var accountablePlan: List<AccountDto>
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class AccountDto (
        var accountCode: String,
        var nameAccount: String,
        var moneyRub: Boolean,
        var report: Boolean,
        var classificator: Boolean,
        var level: Int,
        var childrenAccounts: List<AccountDto>,
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class CompanyDto(
        var enterpriseName: String,
        var dicCategory: String,
        var nit: String,
        var enterpriseLocation: String,
        var emailRepresentative: String,
        var nameRepresentative: String,
        var ciRepresentative: String,
        var numberRepresentative: String,
        var numberRegistration: String,
        var numberEmployee: String,
        var rubro: String,
        var openingDate: String,
        var subsidiaries: List<SubsidiaryDto?>
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class CurrencyConfigDto (
        var currencyList: List<Int>
)


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class SubsidiaryDto (
        var name: String,
        var address: String,
        var areas: List<String?>
)