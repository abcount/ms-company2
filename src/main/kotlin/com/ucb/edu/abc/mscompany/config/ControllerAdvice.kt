package com.ucb.edu.abc.mscompany.config

import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ControllerAdvice: ResponseEntityExceptionHandler() {

    @ExceptionHandler(PostgresException::class)
    fun handlePostgresException(e: PostgresException): ResponseEntity<ResponseDto<Nothing>>{
        val response = ResponseDto<Nothing>(
                null,
                e.message,
                false,
                e.error)
        return ResponseEntity.internalServerError().body(response)
    }


}