package com.ucb.edu.abc.mscompany.entity.aux

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@AllArgsConstructor
data class CompanyDataVoucher(
    val transactionNumber: Int,
    val companyName: String,
){
    constructor(): this(0, "")

    override fun toString(): String {
        return "CompanyDataVoucher(transactionNumber=$transactionNumber, companyName='$companyName')"
    }
}
