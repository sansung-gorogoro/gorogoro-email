package com.gorogoro.notification

import com.gorogoro.notification.rabbitmq.config.MessagingProps
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(MessagingProps::class)
class GorogoroNotificationApplication

fun main(args: Array<String>) {
    runApplication<GorogoroNotificationApplication>(*args)
}
