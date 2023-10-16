package com.ucb.edu.abc.mscompany.config

import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import com.ucb.edu.abc.mscompany.exception.EmptyResponseException
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

    @ExceptionHandler(EmptyResponseException::class)
    fun handleEmptyResponseException(e: EmptyResponseException): ResponseEntity<ResponseDto<Nothing>>{
        val response = ResponseDto<Nothing>(
                null,
                e.message,
                false,
                null)
        return ResponseEntity.status(404).body(response)
    }


}