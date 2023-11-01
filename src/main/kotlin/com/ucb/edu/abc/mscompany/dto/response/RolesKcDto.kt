package com.ucb.edu.abc.mscompany.dto.response

data class RolesKcDto (
    var id: String?,
    var name: String?,
    var description: String?,
    var composite: Boolean?,
    var clientRole: Boolean?,
    var containerId: String?,
    var attributes: Map<Any, Any>?
) {
}