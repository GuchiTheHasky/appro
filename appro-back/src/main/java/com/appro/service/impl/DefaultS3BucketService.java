package com.appro.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.appro.configuration.aws.AwsConfiguration;
import com.appro.entity.Image;
import com.appro.exception.S3OperationException;
import com.appro.service.S3BucketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultS3BucketService implements S3BucketService {

    private final AmazonS3 amazonS3;
    private final AwsConfiguration awsClientConfig;

    @Override
    public String upload(MultipartFile file, Image image) {
        log.info("Starting upload for image ID: {}", image.getId());
        File localFile = convertMultipartFileToFile(file);

        String imageKey = String.valueOf(image.getId());
        try {
            amazonS3.putObject(new PutObjectRequest(awsClientConfig.getBucketName(), imageKey, localFile));
        } catch (Exception e) {
            log.error("Error uploading file to S3 for image ID: {}", image.getId(), e);
            throw new S3OperationException("Failed upload image to S3", e);
        }
        deleteTemporaryFile(localFile);
        String url = getPublicUrl(imageKey);
        log.info("Successfully uploaded image ID: {} to S3 with URL: {}", image.getId(), url);
        return url;
    }

    @Override
    public void delete(Image image) {
        try {
            amazonS3.deleteObject(awsClientConfig.getBucketName(), String.valueOf(image.getId()));
            log.info("Successfully deleted image ID: {} from S3", image.getId());
        } catch (Exception e) {
            log.error("Error deleting image ID: {} from S3", image.getId(), e);
            throw new S3OperationException("Failed to delete image from S3", e);
        }
    }

    String getPublicUrl(String imageKey) {
        return amazonS3.getUrl(awsClientConfig.getBucketName(), imageKey).toString();
    }


    File convertMultipartFileToFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new S3OperationException("Can not convert multipart file cause file name is null.");
        }

        File convertedFile = new File(originalFilename);
        try {
            Files.copy(file.getInputStream(), convertedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log.info("Converted multipart file to local file: {}", convertedFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to convert multipart file to local file", e);
            throw new S3OperationException("Error converting file for upload", e);
        }
        return convertedFile;
    }

    void deleteTemporaryFile(File localFile) {
        try {
            Files.delete(localFile.toPath());
            log.info("Temporary file deleted: {}", localFile.getAbsolutePath());
        } catch (IOException e) {
            log.warn("Failed to delete temporary file: {}", localFile.getAbsolutePath(), e);
        }
    }
}
