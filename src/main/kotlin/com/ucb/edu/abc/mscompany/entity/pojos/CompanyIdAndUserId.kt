package com.ucb.edu.abc.mscompany.entity.pojos

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@AllArgsConstructor
@NoArgsConstructor
data class CompanyIdAndUserId(
    var companyId: Int = 0,
    var companyName: String = "",
    var userId: Int = 0
) {

}