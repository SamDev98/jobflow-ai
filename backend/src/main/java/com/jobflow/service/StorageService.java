package com.jobflow.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

@Slf4j
@Service
public class StorageService {

    @Value("${r2.access-key-id}")
    private String accessKeyId;

    @Value("${r2.secret-access-key}")
    private String secretAccessKey;

    @Value("${r2.bucket-name}")
    private String bucketName;

    @Value("${r2.endpoint}")
    private String endpoint;

    @Value("${r2.public-url}")
    private String publicUrl;

    private AmazonS3 s3Client;

    @PostConstruct
    public void init() {
        if (accessKeyId.isBlank()) {
            log.warn("R2 credentials not configured — storage operations will fail");
            return;
        }
        s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(endpoint, "auto"))
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(accessKeyId, secretAccessKey)))
                .withPathStyleAccessEnabled(true)
                .build();
    }

    public String upload(InputStream content, String key, String contentType, long contentLength) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(contentLength);
        s3Client.putObject(bucketName, key, content, metadata);
        log.info("Uploaded file to R2: {}", key);
        return key;
    }

    public String getPresignedUrl(String key, int expirationHours) {
        Date expiration = new Date(System.currentTimeMillis() + (long) expirationHours * 3600 * 1000);
        URL url = s3Client.generatePresignedUrl(bucketName, key, expiration);
        return url.toString();
    }

    public String getPublicUrl(String key) {
        return publicUrl + "/" + key;
    }
}
