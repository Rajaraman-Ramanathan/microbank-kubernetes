package com.microbank.document.service.impl;

import com.microbank.document.dto.event.TransactionEvent;
import com.microbank.document.dto.response.TransactionDocumentResponse;
import com.microbank.document.model.Document;
import com.microbank.document.repository.DocumentRepository;
import com.microbank.document.response.BaseApiResponse;
import com.microbank.document.service.DocumentService;
import com.microbank.document.service.MinIOService;
import com.microbank.document.service.utils.TransactionDocumentResponseBuilder;
import com.microbank.document.utils.PDFGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final MinIOService minIOService;
    private final TransactionDocumentResponseBuilder transactionDocumentResponseBuilder;
    private static final Logger log =
            LoggerFactory.getLogger(DocumentServiceImpl.class);

    public DocumentServiceImpl(
            DocumentRepository documentRepository,
            MinIOService minIOService,
            TransactionDocumentResponseBuilder transactionDocumentResponseBuilder
    ) {
        this.documentRepository = documentRepository;
        this.minIOService = minIOService;
        this.transactionDocumentResponseBuilder = transactionDocumentResponseBuilder;
    }

    @Override
    public void createTransactionDocumentFromEvent(TransactionEvent event) {
        ByteArrayInputStream pdfStream = PDFGenerator.generateTransactionDocument(event);
        String fileName = "TRANSACTION-" + event.transactionId() + ".pdf";
        String fileUrl = minIOService.uploadFile(fileName, pdfStream, "application/pdf");

        Document document = new Document();
        document.setTransactionId(event.transactionId());
        document.setDocumentName(fileName);
        document.setDocumentUrl(fileUrl);
        document.setSenderAccountIban(event.senderAccountIban());
        document.setReceiverAccountIban(event.receiverAccountIban());
        document.setSenderAccountOwnerName(event.senderAccountOwnerName());
        document.setReceiverAccountOwnerName(event.receiverAccountOwnerName());
        document.setAmount(event.amount());
        document.setDescription(event.description());
        document.setTimestamp(event.timestamp());

        documentRepository.save(document);
    }


    @Override
    public BaseApiResponse<TransactionDocumentResponse> getTransactionDocumentById(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found for ID: " + documentId));
        TransactionDocumentResponse transactionDocumentResponse = transactionDocumentResponseBuilder.buildTransactionDocumentResponse(document);
        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Transaction document with the ID: " + documentId + " retrieved successfully.",
                transactionDocumentResponse
        );
    }

    @Override
    public BaseApiResponse<TransactionDocumentResponse> getTransactionDocumentByTransactionId(UUID transactionId) {
        Document document = documentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Document not found for transaction ID: " + transactionId));
        TransactionDocumentResponse transactionDocumentResponse = transactionDocumentResponseBuilder.buildTransactionDocumentResponse(document);
        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Transaction document associated with the transaction with the ID: " + transactionId + " retrieved successfully.",
                transactionDocumentResponse
        );
    }

    @Override
    public BaseApiResponse<List<TransactionDocumentResponse>> getAllTransactionDocuments() {
        List<Document> documents = documentRepository.findAll();
        List<TransactionDocumentResponse> transactionDocumentResponses = transactionDocumentResponseBuilder.buildTransactionDocumentResponses(documents);
        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "All transaction documents retrieved successfully.",
                transactionDocumentResponses
        );
    }

    @Override
    public void uploadDocument(MultipartFile file, UUID transactionId) {
        try {
            log.info("Uploading document to MinIO | txId={} | file={}",
                    transactionId,
                    file.getOriginalFilename()
            );

            String objectName =
                    transactionId + "/" + file.getOriginalFilename();

            minIOService.uploadFile(
                    objectName,
                    file.getInputStream(),
                    file.getContentType()
            );

            log.info("MinIO upload successful | txId={}", transactionId);

        } catch (Exception e) {
            log.error("Document upload failed | txId={}", transactionId, e);
            throw new RuntimeException("Document upload failed", e);
        }
    }
}
