package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.config.FileStorageProperties;
import com.Jobstream.V0.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final FileStorageProperties fileStorageProperties;

    @Override
    public String storeFile(MultipartFile file, String subfolder) {
        try {
            Path uploadDir = Paths.get(fileStorageProperties.getStoragePath(), subfolder);
            Files.createDirectories(uploadDir);

            String originalFilename = StringUtils.cleanPath(
                    file.getOriginalFilename() != null ? file.getOriginalFilename() : "file"
            );
            String extension = getExtension(originalFilename);
            String filename = UUID.randomUUID().toString() + extension;

            Path targetPath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return fileStorageProperties.getBaseUrl() + "/" + subfolder + "/" + filename;
        } catch (IOException e) {
            log.error("Failed to store file: {}", e.getMessage());
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            String relativePath = fileUrl.replace(fileStorageProperties.getBaseUrl(), "");
            Path filePath = Paths.get(fileStorageProperties.getStoragePath() + relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Failed to delete file: {}", e.getMessage());
        }
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(dotIndex) : "";
    }
}
