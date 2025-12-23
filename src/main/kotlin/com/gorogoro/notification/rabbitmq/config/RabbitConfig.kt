package com.gorogoro.notification.rabbitmq.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.AcknowledgeMode
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Declarables
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.support.converter.DefaultClassMapper
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper.TypePrecedence
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RabbitMqConfig(
    private val props: MessagingProps,
    private val objectMapper: ObjectMapper,
) {

    @Bean
    fun userQueue(): Queue {
        return QueueBuilder.durable(props.queues.user.name)
            .withArgument(QUEUE_TYPE, QUEUE_TYPE_VALUE)
            .withArgument(DEAD_LETTER_EXCHANGE_NAME, props.dlq.dlx)
            .withArgument(DEAD_LETTER_ROUTING_KEY, props.dlq.routing).build()
    }

    @Bean
    fun purchaseQueue(): Queue {
        return QueueBuilder.durable(props.queues.purchase.name)
            .withArgument(QUEUE_TYPE, QUEUE_TYPE_VALUE)
            .withArgument(DEAD_LETTER_EXCHANGE_NAME, props.dlq.dlx)
            .withArgument(DEAD_LETTER_ROUTING_KEY, props.dlq.routing).build()
    }

    @Bean
    fun dlq(): Queue = QueueBuilder.durable(props.dlq.name).build()

    @Bean
    fun rabbitListenerContainerFactory(
        cf: ConnectionFactory,
        messageConverter: MessageConverter,
    ): SimpleRabbitListenerContainerFactory {
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
    fun userQueueBinding(userQueue: Queue, eventExchange: TopicExchange): Declarables {
        return Declarables(
            props.queues.user.bindings.stream().map { rk ->
                BindingBuilder.bind(userQueue).to(eventExchange).with(rk)
            }.toList()
        )
    }

    @Bean
    fun purchaseQueueBinding(purchaseQueue: Queue, eventExchange: TopicExchange): Declarables {
        return Declarables(
            props.queues.purchase.bindings.stream().map { rk ->
                BindingBuilder.bind(purchaseQueue).to(eventExchange).with(rk)
            }.toList()
        )
    }

    @Bean
    fun dlqBinding(dlq: Queue, dlx: TopicExchange): Binding {
        return BindingBuilder.bind(dlq).to(dlx).with(props.dlq.routing)
    }

    @Bean
    fun eventExchange(): TopicExchange = TopicExchange(props.exchange)

    @Bean
    fun dlx(): TopicExchange = TopicExchange(props.dlq.dlx)

    @Bean
    fun messageConverter(): MessageConverter {
        val converter = Jackson2JsonMessageConverter()
        converter.setTypePrecedence(TypePrecedence.INFERRED) // TypeId 무시
        val classMapper = DefaultClassMapper()

        classMapper.setTrustedPackages("*")

        converter.setClassMapper(classMapper)
        return converter
    }

        companion object {
            const val QUEUE_TYPE = "x-queue-type"
            const val QUEUE_TYPE_VALUE = "quorum"
            const val DEAD_LETTER_EXCHANGE_NAME = "x-dead-letter-exchange"
            const val DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key"
            const val CONCURRENCY_CONSUMERS = 3
            const val MAX_CONCURRENCY_CONSUMERS = 10
            const val PREFETCH_COUNT = 10
        }
    }
