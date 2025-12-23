package com.gorogoro.notification.exception.dto

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String
)
