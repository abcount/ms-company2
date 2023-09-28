package com.ucb.edu.abc.mscompany.entity

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@AllArgsConstructor
data class AccountEntity(
        val accountId: Int,
        var companyId: Int,
        var accountAccountId: Int?,
        var codeAccount: String,
        var nameAccount: String,
        var clasificator: Boolean,
        var level: Int,
        var report: Boolean,
        var status: Boolean,
        var moneyRub: Boolean,
){
    constructor(): this (0, 0, 0, "", "", false, 0, false, false, false)

    override fun toString(): String {
        return "AccountEntity(accountId=$accountId, companyId=$companyId, accountAccountId=$accountAccountId, codeAccount='$codeAccount', nameAccount='$nameAccount', clasificator=$clasificator, level=$level, report=$report, status=$status, moneyRub=$moneyRub)"
    }
}
