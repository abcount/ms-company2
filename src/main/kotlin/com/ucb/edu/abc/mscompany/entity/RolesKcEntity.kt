package com.ucb.edu.abc.mscompany.entity

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@AllArgsConstructor
@NoArgsConstructor
data class RolesKcEntity (
    var roleKcId: Long,
    var uuidRole: String,
    var companyId: Int,
    var completeRole: String,
    var roleName: String
){
}