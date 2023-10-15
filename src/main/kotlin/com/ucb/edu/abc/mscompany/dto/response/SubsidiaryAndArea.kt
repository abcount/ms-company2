package com.ucb.edu.abc.mscompany.dto.response

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@AllArgsConstructor
@NoArgsConstructor
data class SubsidiaryDtoRes(
    var subsidiaryId: Int,
    var subsidiaryName: String,
    var areas: MutableList<AreaDtoRes>
)

@Data
@AllArgsConstructor
@NoArgsConstructor
data class AreaDtoRes(
    var areaId: Int,
    var areaName: String,
    var areaSubsidiaryId: Int,
    var status: Boolean = false
)