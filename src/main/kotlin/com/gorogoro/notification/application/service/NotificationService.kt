package com.gorogoro.notification.application.service

import com.gorogoro.notification.application.port.`in`.SendNotificationUseCase
import com.gorogoro.notification.application.port.out.SendEmailPort
import com.gorogoro.notification.domain.model.SendGreetingMailCommand
import org.springframework.stereotype.Service
import java.util.concurrent.ThreadLocalRandom

@Service
class NotificationService(
    private val sendEmailPort: SendEmailPort
) : SendNotificationUseCase {

    override fun sendGreetingEmail(command: SendGreetingMailCommand) {
        sendWelcomeEmail(command.email, command.user)
    }

    private fun sendWelcomeEmail(email: String, name: String) {
        val subject = "Gorogoro에 오신것을 환영합니다!"
        val body = "$name, Thank you for joining us. We are excited to have you on board."
        sendEmailPort.sendEmail(email, subject, body)
    }

    private fun sendVerificationEmail(email: String, code: String) {
        val subject = "[Gorogoro] Email Verification"
        val body = "Your verification code is: $code\nPlease enter this code to verify your email address."
        sendEmailPort.sendEmail(email, subject, body)
    }

    private fun generateVerificationCode(): String {
        return ThreadLocalRandom.current().nextInt(100000, 1000000).toString()
    }
}
