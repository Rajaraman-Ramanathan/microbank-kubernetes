package com.microbank.notification.listeners;

import com.microbank.notification.event.PasswordRecoveryEvent;
import com.microbank.notification.service.MailService;

import jakarta.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PasswordRecoveryListener {

    private final MailService mailService;
    private static final Logger log =
            LoggerFactory.getLogger(
                    PasswordRecoveryListener.class
            );

    public PasswordRecoveryListener(
            MailService mailService
    ) {

        this.mailService = mailService;
    }

    @RabbitListener(
            queues = "password.recovery.queue",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handlePasswordRecoveryEvent(
            PasswordRecoveryEvent event
    ) {
        log.info(
                "Password recovery event received | email={}",
                event.email()
        );
        try {
            mailService.sendPasswordRecoveryMail(
                    event.email(),
                    event.passwordRecoveryCode()
            );
            log.info(
                    "Password recovery email sent successfully | email={}",
                    event.email()
            );
        } catch (MessagingException ex) {
            log.error(
                    "Failed to send password recovery email | email={}",
                    event.email(),
                    ex
            );
            throw new RuntimeException(ex);
        }
    }
}