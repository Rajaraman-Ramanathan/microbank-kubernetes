package com.microbank.document.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microbank.document.dto.event.TransactionEvent;
import com.microbank.document.service.DocumentService;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DocumentListener {

    private final DocumentService documentService;
    private static final Logger log =
        LoggerFactory.getLogger(DocumentListener.class);


    public DocumentListener(DocumentService documentService, ObjectMapper objectMapper) {
        this.documentService = documentService;
    }

    @RabbitListener(queues = "transaction-queue")
    public void handleTransactionMessage(TransactionEvent event) {
        log.info("Raw message received from queue: {}", event);
            documentService.createTransactionDocumentFromEvent(event);
    }

}
