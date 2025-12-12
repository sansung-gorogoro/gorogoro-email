package com.gorogoro.auth.global.exception.dto

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String
)
