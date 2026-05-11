package com.microbank.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.core.TopicExchange;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    private static final Logger log =
            LoggerFactory.getLogger(RabbitMQConfig.class);

    // =========================
    // EXCHANGE
    // =========================

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange("notification.exchange");
    }

    // =========================
    // OBJECT MAPPER
    // =========================

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // =========================
    // MESSAGE CONVERTER
    // =========================

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(
            ObjectMapper objectMapper
    ) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    // =========================
    // RABBIT TEMPLATE
    // =========================

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter jsonMessageConverter
    ) {
        RabbitTemplate rabbitTemplate =
                new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        // Mandatory delivery
        rabbitTemplate.setMandatory(true);
        // Publisher confirms
        if (connectionFactory instanceof CachingConnectionFactory ccf) {
            ccf.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
            ccf.setPublisherReturns(true);
        }
        // Confirm callback
        rabbitTemplate.setConfirmCallback(
                (
                        CorrelationData correlationData,
                        boolean ack,
                        String cause
                ) -> {
                    if (ack) {
                        log.info(
                                "RabbitMQ message published successfully | correlationId={}",
                                correlationData != null
                                        ? correlationData.getId()
                                        : "N/A"
                        );
                    } else {
                        log.error(
                                "RabbitMQ message publish failed | correlationId={} | cause={}",
                                correlationData != null
                                        ? correlationData.getId()
                                        : "N/A",
                                cause
                        );
                    }
                }
        );
        // Return callback
        rabbitTemplate.setReturnsCallback(returned ->
                log.error(
                        "RabbitMQ returned message | exchange={} | routingKey={} | replyCode={} | replyText={}",
                        returned.getExchange(),
                        returned.getRoutingKey(),
                        returned.getReplyCode(),
                        returned.getReplyText()
                )
        );
        return rabbitTemplate;
    }
}