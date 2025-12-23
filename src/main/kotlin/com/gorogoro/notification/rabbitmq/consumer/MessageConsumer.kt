package com.gorogoro.notification.rabbitmq.consumer

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidNullException
import com.gorogoro.auth.global.exception.BusinessException
import com.gorogoro.auth.global.exception.ErrorCode
import com.gorogoro.notification.application.port.`in`.SendNotificationUseCase
import com.gorogoro.notification.domain.model.SendGreetingMailCommand
import com.gorogoro.notification.rabbitmq.domain.dto.EventEnvelope
import com.gorogoro.notification.rabbitmq.domain.dto.event.Event
import com.gorogoro.notification.rabbitmq.domain.dto.event.SendGreetingMailEvent
import com.rabbitmq.client.Channel
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageBuilder
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class MessageConsumer(
    private val sendNotificationUseCase: SendNotificationUseCase,
    private val rabbitTemplate: RabbitTemplate,
    private val objectMapper: ObjectMapper
) {

    @RabbitListener(queues = ["\${rabbit.events.queues.user.name}"], ackMode = "MANUAL")
    @Throws(IOException::class)
    fun handle(message: Message, channel: Channel) {
        val tag = message.messageProperties.deliveryTag

        val envelope: EventEnvelope<Event> = objectMapper.readValue(
            message.body,
            object : TypeReference<EventEnvelope<Event>>() {}
        )

        val event = envelope.payload

        try{
            when (event) {
                is SendGreetingMailEvent -> {
                    val command = SendGreetingMailCommand(event.email,event.user)
                    handleNotificationCommand(command, message)
                    channel.basicAck(tag, false)
                }

                else -> {
                    channel.basicNack(tag, false, false);
                    throw BusinessException.builder(ErrorCode.EVENT_HAS_NULL).build()
                }
            }
        }catch (e : Exception) {
            channel.basicNack(tag, false, false);
            //TODO나중에 로깅 추가
        }
    }

    private fun handleNotificationCommand(command: SendGreetingMailCommand, message: Message) {
        sendNotificationUseCase.sendGreetingEmail(command)

        if (message.messageProperties.replyTo != null) {
            val replyTo = message.messageProperties.replyTo
            val correlationId = message.messageProperties.correlationId

            val responseMessage = MessageBuilder
                .withBody(null)
                .setCorrelationId(correlationId)
                .build()

            rabbitTemplate.send("", replyTo, responseMessage)
        }
    }
}
