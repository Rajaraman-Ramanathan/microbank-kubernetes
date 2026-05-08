package com.microbank.document.service.impl;

import com.microbank.document.exception.CustomException;
import com.microbank.document.service.MinIOService;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

@Service
public class MinIOServiceImpl implements MinIOService {

    private static final Logger log =
            LoggerFactory.getLogger(MinIOServiceImpl.class);

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public MinIOServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    public void validateBucket() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            if (!exists) {
                log.error(
                        "Required MinIO bucket does not exist | bucket={}",
                        bucketName
                );
                throw new IllegalStateException(
                        "Required MinIO bucket missing: " + bucketName
                );
            }
            log.info(
                    "MinIO bucket validation successful | bucket={}",
                    bucketName
            );
        } catch (Exception ex) {
            log.error(
                    "Failed to validate MinIO bucket | bucket={}",
                    bucketName,
                    ex
            );
            throw new IllegalStateException(
                    "MinIO bucket validation failed",
                    ex
            );
        }
    }

    @Override
    public String uploadFile(String fileName, InputStream fileStream, String contentType) {
        try {
        log.info(
                "Uploading file to MinIO | bucket={} | object={}",
                bucketName,
                fileName
        );
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(fileStream, -1, 10 * 1024 * 1024)
                        .contentType(contentType)
                        .build()
        );
        log.info(
                "MinIO upload successful | bucket={} | object={}",
                bucketName,
                fileName
        );
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .method(Method.GET)
                        .build()
        );
    } catch (Exception ex) {
        log.error(
                "MinIO upload failed | bucket={} | object={}",
                bucketName,
                fileName,
                ex
        );
        throw new CustomException(
                "MinIO upload failed for object: " + fileName
        );
    }
  }
}
