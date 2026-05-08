package com.microbank.notification.listeners;

import com.microbank.notification.event.ActivationEvent;
import com.microbank.notification.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ActivationListener {

    private static final Logger log =
            LoggerFactory.getLogger(ActivationListener.class);

    private final MailService mailService;

    public ActivationListener(MailService mailService) {
        this.mailService = mailService;
    }

    @RabbitListener(
            queues = "activation.queue",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleActivationEvent(ActivationEvent event) {

        log.info(
                "Activation event received | email={}",
                event.email()
        );

        try {

            mailService.sendActivationMail(
                    event.email(),
                    event.firstName(),
                    event.lastName(),
                    event.activationCode()
            );

            log.info(
                    "Activation email sent successfully | email={}",
                    event.email()
            );

        } catch (Exception ex) {

            log.error(
                    "Failed to send activation email | email={}",
                    event.email(),
                    ex
            );

            throw new RuntimeException(ex);
        }
    }
}