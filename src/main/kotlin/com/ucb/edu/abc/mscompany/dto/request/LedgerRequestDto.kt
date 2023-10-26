package com.ucb.edu.abc.mscompany.dto.request
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.util.*

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class LedgerRequestDto(
        var subsidiaries: MutableList<Int>,
        var areas: MutableList<Int>,
        var from: Date,
        var to: Date,
        var accountsId: MutableList<Int>,
        var currencies: MutableList<Int>,
) {
}