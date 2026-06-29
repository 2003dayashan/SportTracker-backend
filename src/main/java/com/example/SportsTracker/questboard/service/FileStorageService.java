package com.example.SportsTracker.questboard.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service for handling file uploads and downloads.
 */
@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    /**
     * Constructor that initializes the upload directory.
     * @param uploadDir The directory where files will be stored
     */
    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    /**
     * Stores a file and returns its unique filename.
     * @param file The file to store
     * @return The unique filename of the stored file
     * @throws IOException If an I/O error occurs
     */
    public String storeFile(MultipartFile file) throws IOException {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Validate file extension
        String fileExtension = getFileExtension(fileName).toLowerCase();
        if (!isAllowedFileExtension(fileExtension)) {
            throw new IOException("Invalid file type. Only JPG, PNG, PDF, and DOC files are allowed.");
        }

        // Generate a unique filename to prevent collisions
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = fileName.substring(dotIndex);
        }
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // Copy file to the target location (Replacing existing file with the same name)
        Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName;
    }

    /**
     * Loads a file as a Resource.
     * @param fileName The name of the file to load
     * @return The file as a Resource
     * @throws MalformedURLException If the file cannot be found
     */
    public Resource loadFileAsResource(String fileName) throws MalformedURLException {
        Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if(resource.exists()) {
            return resource;
        } else {
            throw new RuntimeException("File not found: " + fileName);
        }
    }

    /**
     * Gets the file path for a given filename.
     * @param fileName The name of the file
     * @return The Path to the file
     */
    public Path getFilePath(String fileName) {
        return this.fileStorageLocation.resolve(fileName).normalize();
    }

    /**
     * Gets the content type for a given filename.
     * @param fileName The name of the file
     * @return The content type (MIME type)
     */
    public String getContentType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "pdf" -> "application/pdf";
            case "doc", "docx" -> "application/msword";
            default -> "application/octet-stream";
        };
    }

    /**
     * Extracts the file extension from a filename.
     * @param fileName The filename
     * @return The file extension (without the dot), or empty string if no extension
     */
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }

    /**
     * Checks if the file extension is allowed.
     * @param extension The file extension (without dot, lowercase)
     * @return true if allowed, false otherwise
     */
    private boolean isAllowedFileExtension(String extension) {
        return switch (extension) {
            case "jpg", "jpeg", "png", "pdf", "doc", "docx" -> true;
            default -> false;
        };
    }
}