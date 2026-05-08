package com.microbank.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean; 
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // =========================
    // EXCHANGES
    // =========================

    @Bean
    public TopicExchange transactionExchange() {
        return new TopicExchange("transaction.exchange");
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange("deadletter.exchange");
    }

    // =========================
    // QUEUES
    // =========================

    @Bean
    public Queue transactionNotificationQueue() {
        return QueueBuilder.durable("transaction.notification.queue")
                .withArgument("x-dead-letter-exchange", "deadletter.exchange")
                .withArgument("x-dead-letter-routing-key", "transaction.notification.dead")
                .build();
    }

    @Bean
    public Queue transactionNotificationDLQ() {
        return QueueBuilder.durable("transaction.notification.dlq").build();
    }

    // =========================
    // BINDINGS
    // =========================

    @Bean
    public Binding transactionNotificationBinding() {
        return BindingBuilder
                .bind(transactionNotificationQueue())
                .to(transactionExchange())
                .with("transaction.completed");
    }

    @Bean
    public Binding transactionNotificationDLQBinding() {
        return BindingBuilder
                .bind(transactionNotificationDLQ())
                .to(deadLetterExchange())
                .with("transaction.notification.dead");
    }

    // =========================
    // MESSAGE CONVERTER
    // =========================

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // =========================
    // RETRY + LISTENER FACTORY
    // =========================

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(5);

        return factory;
    }
}