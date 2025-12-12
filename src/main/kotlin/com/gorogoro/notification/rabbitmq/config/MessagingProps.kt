package com.gorogoro.notification.rabbitmq.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "rabbit.events")
data class MessagingProps(
    val exchange: String,
    val routingPrefix: String,
    val queues: Queue,
    val dlq: DeadLetters
){
    fun toRoutingKey(eventType: String): String {
        if(routingPrefix.isEmpty()){
            return eventType
        }
        return if(routingPrefix.endsWith(".")){
            routingPrefix + eventType
        }else routingPrefix
    }

    data class Queue(
        val purchase: QueueProps,
        val user: QueueProps
    )

    data class QueueProps(
        val name: String,
        val bindings: List<String>
    )

    data class DeadLetters(
        val dlx: String,
        val name: String,
        val routing: String
    )
}
