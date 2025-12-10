package com.gorogoro.notification.domain.model

data class NotificationCommand(
    val email: String,
    val type: EmailType,
    val payload: Map<String, Any> = emptyMap()
) : EventCommand
