package com.ucb.edu.abc.mscompany.dto.response

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@AllArgsConstructor
@NoArgsConstructor
data class ResponseDto <T>(
        val data: T?,
        val message: String?,
        val success: Boolean,
        val errors: String?
)
