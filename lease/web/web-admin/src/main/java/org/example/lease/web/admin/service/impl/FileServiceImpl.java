package org.example.lease.web.admin.service.impl;

import io.minio.*;
import io.minio.errors.*;
import org.example.lease.common.minio.MinioProperties;
import org.example.lease.web.admin.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

        @Autowired
        private MinioClient minioClient;

        @Autowired
        private MinioProperties minioProperties;

        @Override
        public String upload(MultipartFile file) throws ServerException, InsufficientDataException,
                        ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
                        InvalidResponseException, XmlParserException, InternalException {

                boolean b = minioClient
                                .bucketExists(BucketExistsArgs
                                                .builder()
                                                .bucket(minioProperties.getBucketName())
                                                .build());

                if (!b) {
                        minioClient.makeBucket(MakeBucketArgs
                                        .builder()
                                        .bucket(minioProperties.getBucketName())
                                        .build());
                        minioClient.setBucketPolicy(SetBucketPolicyArgs
                                        .builder()
                                        .bucket(minioProperties.getBucketName())
                                        .config(createBucketPolicyConfig(minioProperties.getBucketName()))
                                        .build());
                }
                String filename = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + UUID.randomUUID() + "-"
                                + file.getOriginalFilename();
                minioClient.putObject(PutObjectArgs
                                .builder()
                                .bucket(minioProperties.getBucketName())
                                .stream(file.getInputStream(), file.getSize(), -1)
                                .object(filename)
                                .contentType(file.getContentType())
                                .build());

                // 使用externalUrl（如果配置）或者endpoint作为基础URL
                String baseUrl = minioProperties.getExternalUrl() != null && !minioProperties.getExternalUrl().isEmpty()
                                ? minioProperties.getExternalUrl()
                                : minioProperties.getEndpoint();
                String url = baseUrl + "/" + minioProperties.getBucketName() + "/" + filename;
                return url;

        }

        private String createBucketPolicyConfig(String bucketName) {

                return """
                                {
                                  "Statement" : [ {
                                    "Action" : "s3:GetObject",
                                    "Effect" : "Allow",
                                    "Principal" : "*",
                                    "Resource" : "arn:aws:s3:::%s/*"
                                  } ],
                                  "Version" : "2012-10-17"
                                }
                                """.formatted(bucketName);
        }
}
