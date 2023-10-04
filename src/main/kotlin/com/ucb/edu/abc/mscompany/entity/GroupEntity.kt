package com.ucb.edu.abc.mscompany.entity

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.time.LocalDate

@Data
@AllArgsConstructor
@NoArgsConstructor
data class GroupEntity(
    var groupId: Int = 0,
    var name: String = "",
    var description: String= "",
    var diccCategory: String? = "",
    var status : Boolean ? = false,
    var dateCreated: LocalDate = LocalDate.now(),
    var commonId :Int = 0 // FIXME this will be company ID
) {

}