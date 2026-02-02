package com.microbank.document.controller;
import com.microbank.document.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@RestController
@RequestMapping("/internal/documents")
public class InternalDocumentController {

    private final DocumentService documentService;
    private static final Logger log =
        LoggerFactory.getLogger(InternalDocumentController.class);

    public InternalDocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(
        value = "/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> uploadDocument(
            @RequestPart("file") MultipartFile file,
            @RequestParam("transactionId") UUID transactionId
    ) {
        log.info("INTERNAL document upload request received | file={} | size={} | txId={}",
                file.getOriginalFilename(),
                file.getSize(),
                transactionId
        );

                documentService.uploadDocument(file, transactionId);
        
                log.info("INTERNAL document upload completed | txId={}", transactionId);
                return ResponseEntity.ok().build();
            }
        }