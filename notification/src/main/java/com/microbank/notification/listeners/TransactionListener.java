package com.microbank.notification.listeners;

import com.microbank.notification.event.TransactionEvent;
import com.microbank.notification.service.MailService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private final MailService mailService;
    private static final Logger log = LoggerFactory.getLogger(TransactionListener.class);

    public TransactionListener(MailService mailService) {
        this.mailService = mailService;
    }

    @RabbitListener(
            queues = "transaction.notification.queue",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleTransactionEvent(TransactionEvent event) {

        log.info(
                "Transaction event received | txId={} | sender={} | receiver={}",
                event.transactionId(),
                event.senderAccountEmail(),
                event.receiverAccountEmail()
        );

        try {

            // Sender notification
            mailService.sendTransactionMail(
                    event.senderAccountEmail(),
                    event.transactionId(),
                    event.senderAccountOwnerName(),
                    event.receiverAccountOwnerName(),
                    event.senderAccountIban(),
                    event.receiverAccountIban(),
                    event.amount(),
                    event.description(),
                    event.timestamp()
            );

            // Receiver notification
            mailService.sendTransactionMail(
                    event.receiverAccountEmail(),
                    event.transactionId(),
                    event.senderAccountOwnerName(),
                    event.receiverAccountOwnerName(),
                    event.senderAccountIban(),
                    event.receiverAccountIban(),
                    event.amount(),
                    event.description(),
                    event.timestamp()
            );

            log.info(
                    "Transaction notification emails sent successfully | txId={}",
                    event.transactionId()
            );

        } catch (MessagingException ex) {

            log.error(
                    "Failed to send transaction email | txId={}",
                    event.transactionId(),
                    ex
            );

            throw new RuntimeException(ex);
        }
    }
}