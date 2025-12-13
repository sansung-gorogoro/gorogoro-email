package com.gorogoro.notification.rabbitmq.domain.dto

import com.gorogoro.notification.rabbitmq.domain.dto.event.Event
import java.time.Instant
import java.util.UUID

data class EventEnvelope<T : Event>(
    val eventId: UUID,
    val occurredAt: Instant,
    val payload: T,
    val metadata: Map<String, String> = emptyMap()
) {
    companion object {
        fun <T : Event> wrap(payload: T): EventEnvelope<T> {
            return EventEnvelope(
                eventId = UUID.randomUUID(),
                occurredAt = Instant.now(),
                payload = payload,
                metadata = emptyMap()
            )
        }
    }
    fun withMetadata(additional: Map<String, String>?): EventEnvelope<T> {
        if (additional.isNullOrEmpty()) {
            return this
        }
        return copy(metadata = this.metadata + additional)
    }

    val type: String
        get() = payload.type

    val version: String
        get() = payload.version()
}
