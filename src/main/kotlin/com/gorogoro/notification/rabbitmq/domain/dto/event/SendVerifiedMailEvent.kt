package com.gorogoro.notification.rabbitmq.domain.dto.event

@ConsistentCopyVisibility
data class SendVerifiedMailEvent private constructor(
    val email: String,
    val user: String,
    val code: String,
    override val type: String ="user.verified.send-email"
): Event {

    companion object {
        fun of(email: String, username: String, code: String): SendVerifiedMailEvent {
            return SendVerifiedMailEvent(email, username, code)
        }
    }

    override fun version(): String {
        return super.version()
    }
}
