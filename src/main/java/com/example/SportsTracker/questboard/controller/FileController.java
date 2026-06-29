package com.example.SportsTracker.questboard.controller;

import com.example.SportsTracker.questboard.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Controller for handling file uploads and downloads.
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * Uploads a file.
     * @param file The file to upload
     * @return The filename of the uploaded file
     * @throws IOException If an I/O error occurs
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = fileStorageService.storeFile(file);
        return ResponseEntity.ok().body(fileName);
    }

    /**
     * Downloads a file by filename.
     * @param fileName The name of the file to download
     * @return The file as a downloadable resource
     * @throws IOException If an I/O error occurs
     */
    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Determine file content type
        String contentType = fileStorageService.getContentType(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}