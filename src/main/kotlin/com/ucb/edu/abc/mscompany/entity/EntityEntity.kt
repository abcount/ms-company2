package com.ucb.edu.abc.mscompany.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class EntityEntity(
        val entityId: Int,
        var companyId: Int,
        var entityName: String,
        var nit: String,
        var socialReason: String,
        var foreign: Boolean,
)
