package com.thlogistic.object_storage.adapters.controllers;

import com.thlogistic.object_storage.core.domain.FileMetadata;
import com.thlogistic.object_storage.core.services.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ObjectStorageController extends BaseController implements ObjectStorageResource {

    private final AmazonS3Service amazonS3Service;

    @Override
    public ResponseEntity<Object> uploadFiles(List<MultipartFile> files) {
        List<FileMetadata> result = amazonS3Service.createFiles(files);
        return successResponse(result, null);
    }

    @Override
    public ResponseEntity<Object> downloadFile(String filename) {
        ByteArrayOutputStream downloadInputStream = amazonS3Service.download(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(downloadInputStream.toByteArray());
    }
}