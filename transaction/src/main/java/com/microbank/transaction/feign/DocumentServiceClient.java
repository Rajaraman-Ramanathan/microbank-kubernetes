package com.microbank.transaction.feign;

import com.microbank.transaction.config.FeignConfig;
import com.microbank.transaction.config.FeignMultipartConfig;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
    name = "document-service",
    configuration = {FeignConfig.class, FeignMultipartConfig.class}
)
public interface DocumentServiceClient {

    @PostMapping(
        value = "/internal/documents/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    void uploadDocument(
        @RequestPart("file") MultipartFile file,
        @RequestParam("transactionId") String transactionId
    );
}

