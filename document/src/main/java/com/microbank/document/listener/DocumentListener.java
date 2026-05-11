package com.microbank.document.listener;

import com.microbank.document.dto.event.TransactionEvent;
import com.microbank.document.service.DocumentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DocumentListener {

    private static final Logger log =
            LoggerFactory.getLogger(DocumentListener.class);

    private final DocumentService documentService;

    public DocumentListener(DocumentService documentService) {
        this.documentService = documentService;
    }

    @RabbitListener(
            queues = "transaction.document.queue",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleTransactionEvent(TransactionEvent event) {
        log.info(
                "Transaction event received for document generation | txId={}",
                event.transactionId()
        );
        try {
            documentService.createTransactionDocumentFromEvent(event);
            log.info(
                    "Transaction document generated successfully | txId={}",
                    event.transactionId()
            );
        } catch (Exception ex) {
            log.error(
                    "Failed to generate transaction document | txId={}",
                    event.transactionId(),
                    ex
            );
            throw new RuntimeException(ex);
        }
    }
}