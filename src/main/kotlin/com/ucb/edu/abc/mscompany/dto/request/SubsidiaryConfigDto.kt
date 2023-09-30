package com.ucb.edu.abc.mscompany.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class SubsidiaryConfigDto (
        var subsidiaries: List<AddSubsidiaryDto>,
        var areas: List<AreaDto>
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class AddSubsidiaryDto (
        var subsidiaryId: Int,
        var address: String,
        var subsidiaryName: String,
)

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class AreaDto (
        var areaId: Int,
        var areaName: String,
)


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class DeleteAreasDto (
        var areas: List<Int>,
        var subsidiaries: List<Int>
)
