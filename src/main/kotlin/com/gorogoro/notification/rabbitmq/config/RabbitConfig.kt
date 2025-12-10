package com.gorogoro.notification.rabbitmq.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.amqp.core.AcknowledgeMode
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMqConfig {

    @Bean
    fun pocQueue(): Queue {
        return QueueBuilder.durable(QUEUE_NAME)
            .withArgument(QUEUE_TYPE, QUEUE_TYPE_VALUE)
            .withArgument(DEAD_LETTER_EXCHANGE_NAME, DLX_NAME)
            .withArgument(DEAD_LETTER_ROUTING_KEY, DLQ_ROUTING_KEY)
            .build()
    }

    @Bean
    fun pocQueue2(): Queue {
        return QueueBuilder.durable(QUEUE_NAME2)
            .withArgument(QUEUE_TYPE, QUEUE_TYPE_VALUE)
            .withArgument(DEAD_LETTER_EXCHANGE_NAME, DLX_NAME)
            .withArgument(DEAD_LETTER_ROUTING_KEY, DLQ_ROUTING_KEY)
            .build()
    }

    @Bean
    fun pocDlq(): Queue = QueueBuilder.durable(DLQ_NAME).build()

    @Bean
    fun rabbitListenerContainerFactory(cf: ConnectionFactory,messageConverter: MessageConverter): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(cf)
        factory.setConcurrentConsumers(CONCURRENCY_CONSUMERS)
        factory.setMaxConcurrentConsumers(MAX_CONCURRENCY_CONSUMERS)
        factory.setPrefetchCount(PREFETCH_COUNT)
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL)
        factory.setMessageConverter(messageConverter)
        return factory
    }

    @Bean
    fun pocQueueBinding(pocQueue: Queue, pocExchange: TopicExchange): Binding {
        return BindingBuilder.bind(pocQueue).to(pocExchange).with(ROUTING_KEY)
    }

    @Bean
    fun pocQueue2Binding(pocQueue2: Queue, pocExchange: TopicExchange): Binding {
        return BindingBuilder.bind(pocQueue2).to(pocExchange).with(ROUTING_KEY)
    }

    @Bean
    fun pocDlqBinding(pocDlq: Queue, pocDlx: TopicExchange): Binding {
        return BindingBuilder.bind(pocDlq).to(pocDlx).with(DLQ_ROUTING_KEY)
    }

    @Bean
    fun pocExchange(): TopicExchange = TopicExchange(EXCHANGE_NAME)

    @Bean
    fun pocDlx(): TopicExchange = TopicExchange(DLX_NAME)

    @Bean
    fun messageConverter(): Jackson2JsonMessageConverter {
        val objectMapper = ObjectMapper().apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

            registerModule(JavaTimeModule())

            registerKotlinModule()
        }

        return Jackson2JsonMessageConverter(objectMapper).apply {
            setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED)
        }
    }

    companion object {
        const val QUEUE_NAME = "poc.spring.queue"
        const val QUEUE_NAME2 = "poc.spring.queue2"
        const val DLQ_NAME = "poc.spring.queue.dlq"

        const val EXCHANGE_NAME = "poc.spring.exchange"
        const val DLX_NAME = "poc.spring.dlx"

        const val ROUTING_KEY = "poc.spring.routing"
        const val DLQ_ROUTING_KEY = "poc.spring.routing.dlq"

        const val QUEUE_TYPE ="x-queue-type"
        const val QUEUE_TYPE_VALUE = "quorum"
        const val DEAD_LETTER_EXCHANGE_NAME = "x-dead-letter-exchange"
        const val DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key"

        const val CONCURRENCY_CONSUMERS = 3
        const val MAX_CONCURRENCY_CONSUMERS = 10
        const val PREFETCH_COUNT = 10
    }
}