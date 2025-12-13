package com.gorogoro.notification.application.port.`in`

import com.gorogoro.notification.domain.model.SendGreetingMailCommand

interface SendNotificationUseCase {
    fun sendGreetingEmail(command: SendGreetingMailCommand)
}
