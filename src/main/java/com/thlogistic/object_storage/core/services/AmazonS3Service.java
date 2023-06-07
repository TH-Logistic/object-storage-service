package com.thlogistic.object_storage.core.services;

import com.amazonaws.services.s3.model.S3Object;
import com.thlogistic.object_storage.core.domain.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface AmazonS3Service {
    List<FileMetadata> createFiles(List<MultipartFile> files);

    ByteArrayOutputStream download(String fileName);
}
