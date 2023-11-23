package com.ucb.edu.abc.mscompany.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.util.Date

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class SumasSaldosRequestDto (
    var subsidiaries: MutableList<Int>,
    var areas: MutableList<Int>,
    var from: String,
    var to: String,
    var currencies: String,
) {
}