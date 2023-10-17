package com.ucb.edu.abc.mscompany.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class AuxiliaryDataDto(
        var auxiliaryId: Int?,
        var auxiliaryName: String,
        var auxiliaryCode: String,
)


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class EntityDataDto(
        val entityId: Int?,
        var entityName: String,
        var entityNit: String,
        var entitySocialReason: String,
        var foreign: Boolean,
)

