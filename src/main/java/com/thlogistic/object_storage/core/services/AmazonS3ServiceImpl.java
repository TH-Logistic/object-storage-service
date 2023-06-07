package com.thlogistic.object_storage.core.services;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.thlogistic.object_storage.aop.exception.CustomRuntimeException;
import com.thlogistic.object_storage.core.domain.FileMetadata;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AmazonS3ServiceImpl implements AmazonS3Service {

    private final AmazonS3 s3Client;
    private final Tika tika = new Tika();

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public List<FileMetadata> createFiles(List<MultipartFile> files) {
        return files.stream()
                .map(file -> put(bucketName, file.getOriginalFilename(), file, true))
                .collect(Collectors.toList());
    }

    private FileMetadata put(String bucket, String key, MultipartFile file, Boolean publicAccess) {
        FileMetadata metadata = FileMetadata.builder()
                .bucket(bucket)
                .key(key)
                .name(file.getOriginalFilename())
                .extension(StringUtils.getFilenameExtension(file.getOriginalFilename()))
                .mime(tika.detect(file.getOriginalFilename()))
                .size(file.getSize())
                .build();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(metadata.getSize());
        objectMetadata.setContentType(metadata.getMime());

        try {
            InputStream stream = file.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, stream, objectMetadata);
            PutObjectResult putObjectResult = s3Client.putObject(putObjectRequest);
            metadata.setUrl(s3Client.getUrl(bucket, key).toString());
            metadata.setHash(putObjectResult.getContentMd5());
            metadata.setEtag(putObjectResult.getETag());
            metadata.setPublicAccess(publicAccess);
        } catch (IOException e) {
            throw new CustomRuntimeException("An error occurred when put files to S3");
        }
        return metadata;
    }

    @Override
    public ByteArrayOutputStream download(String fileName) {
        try {
            S3Object s3object = s3Client.getObject(new GetObjectRequest(bucketName, fileName));

            InputStream is = s3object.getObjectContent();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[4096];
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            return outputStream;
        } catch (IOException ioException) {
            throw new CustomRuntimeException("IOException: " + ioException.getMessage());
        } catch (AmazonServiceException serviceException) {
            throw new CustomRuntimeException("AmazonServiceException: " + serviceException.getMessage());
        } catch (AmazonClientException clientException) {
            throw new CustomRuntimeException("AmazonClientException: " + clientException.getMessage());
        }
    }
}
