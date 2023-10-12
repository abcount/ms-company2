package com.ucb.edu.abc.mscompany.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class NewInvitationDto (
    var userId: Int,
    var subsidiaries: MutableList<Int>,
    var areas: MutableList<Int>,
    var permissions: MutableList<Int>
){
}