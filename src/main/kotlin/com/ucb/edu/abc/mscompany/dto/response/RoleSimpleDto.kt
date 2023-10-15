package com.ucb.edu.abc.mscompany.dto.response

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@AllArgsConstructor
data class RoleSimpleDto(
    var roleId: Int,
    var roleShortName: String,
    var roleDescription: String,
) {
}