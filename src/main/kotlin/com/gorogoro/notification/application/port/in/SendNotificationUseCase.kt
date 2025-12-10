package com.gorogoro.notification.application.port.`in`

import com.gorogoro.notification.domain.model.EventCommand

interface SendNotificationUseCase {
    fun sendNotification(command: EventCommand): String?
}
