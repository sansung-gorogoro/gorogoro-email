package com.gorogoro.notification.rabbitmq.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "eventType"
)@JsonSubTypes(
    JsonSubTypes.Type(value = NotificationEventDto::class, name = "EMAIL"),
)
sealed interface Event