package com.gorogoro.auth.global.exception

import com.gorogoro.auth.global.exception.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ErrorResponse> {

        val response = ErrorResponse(
            status = e.httpStatus.value(),
            error = e.httpStatus.reasonPhrase,
            message = e.message
        )

        return ResponseEntity.status(e.httpStatus).body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {

        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val response = ErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = "Internal Server Error : ${e.stackTraceToString()}"
        )

        return ResponseEntity.status(status).body(response)
    }
}
