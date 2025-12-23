package com.gorogoro.notification.rabbitmq.domain.dto.event

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes(JsonSubTypes.Type(value = SendGreetingMailEvent::class, name = "user.greeting.send-email"))
sealed interface Event {
    val type: String

    fun version(): String = "v1"
}