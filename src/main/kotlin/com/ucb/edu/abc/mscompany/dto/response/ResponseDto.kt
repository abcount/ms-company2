package com.ucb.edu.abc.mscompany.dto.response

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@AllArgsConstructor
@NoArgsConstructor
data class ResponseDto <T>(
        var data: T?,
        var message: String?,
        var success: Boolean,
        var errors: String?
)
