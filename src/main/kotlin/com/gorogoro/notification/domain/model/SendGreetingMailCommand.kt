package com.gorogoro.notification.domain.model

data class SendGreetingMailCommand(
    val email: String,
    val user: String
)
