package com.thlogistic.object_storage.adapters.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/object-storage")
interface ObjectStorageResource {
    @PostMapping("/files")
    ResponseEntity<Object> uploadFiles(@RequestPart(value = "files") List<MultipartFile> files);

    @GetMapping("/download/{filename}")
    ResponseEntity<Object> downloadFile(@PathVariable String filename);
}
