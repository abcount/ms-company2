package com.ucb.edu.abc.mscompany.entity

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.time.LocalDate

@Data
@AllArgsConstructor
@NoArgsConstructor
data class RoleEntity(
    var roleId: Int = 0,
    var name: String = "",
    var description: String = "",
    var diccCategory: String? = null,
    var status : Boolean ? = null,
    var dateCreated: LocalDate = LocalDate.now(),
    var commomId :Int = 0
) {
}