package com.microbank.document.config;

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
    public Queue transactionDocumentQueue() {

        return QueueBuilder.durable("transaction.document.queue")
                .withArgument("x-dead-letter-exchange","deadletter.exchange")
                .withArgument("x-dead-letter-routing-key","transaction.document.dead")
                .build();
    }

    @Bean
    public Queue transactionDocumentDLQ() {
        return QueueBuilder.durable("transaction.document.dlq").build();
    }

    // =========================
    // BINDINGS
    // =========================

    @Bean
    public Binding transactionDocumentBinding() {
        return BindingBuilder
                .bind(transactionDocumentQueue())
                .to(transactionExchange())
                .with("transaction.completed");
    }

    @Bean
    public Binding transactionDocumentDLQBinding() {

        return BindingBuilder
                .bind(transactionDocumentDLQ())
                .to(deadLetterExchange())
                .with("transaction.document.dead");
    }

    // =========================
    // MESSAGE CONVERTER
    // =========================

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // =========================
    // LISTENER FACTORY
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