package com.ucb.edu.abc.mscompany.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class AccountablePlanDto(
    var accountId: Int,
    var accountCode: String,
    var nameAccount: String,
    var moneyRub: Boolean,
    var report : Boolean,
    var clasificator: Boolean,
    var level: Int,
    var editable: Boolean,
    var childrenAccounts: MutableList<AccountablePlanDto>

) {
    constructor(): this(0,"", "", false, false, false, 0, false, mutableListOf())




    
}