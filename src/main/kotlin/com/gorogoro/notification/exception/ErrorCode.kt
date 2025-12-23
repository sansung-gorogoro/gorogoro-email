package com.gorogoro.notification.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String
) {
    EVENT_HAS_NULL(HttpStatus.INTERNAL_SERVER_ERROR, "Event has not been provided"),
}
