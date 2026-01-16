package com.job.service.impl;

import com.job.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final String CV_DIR = "cvs";
    private static final String PROFILE_PIC_DIR = "profile-pictures";
    private static final String COMPANY_LOGO_DIR = "company-logos";

    @Override
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file");
        }

        String fileName = generateUniqueFileName(file.getOriginalFilename());
        Path uploadPath = Paths.get(uploadDir, directory);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        log.info("Uploaded file: {} to directory: {}", fileName, directory);
        return directory + "/" + fileName;
    }

    @Override
    public String uploadCV(MultipartFile file, Long userId) throws IOException {
        validateFileType(file, "pdf");
        return uploadFile(file, CV_DIR + "/" + userId);
    }

    @Override
    public String uploadProfilePicture(MultipartFile file, Long userId) throws IOException {
        validateImageType(file);
        return uploadFile(file, PROFILE_PIC_DIR);
    }

    @Override
    public String uploadCompanyLogo(MultipartFile file, Long companyId) throws IOException {
        validateImageType(file);
        return uploadFile(file, COMPANY_LOGO_DIR);
    }

    @Override
    public void deleteFile(String filePath) throws IOException {
        Path path = getFilePath(filePath);
        if (Files.exists(path)) {
            Files.delete(path);
            log.info("Deleted file: {}", filePath);
        }
    }

    @Override
    public boolean fileExists(String filePath) {
        return Files.exists(getFilePath(filePath));
    }

    @Override
    public Path getFilePath(String fileName) {
        return Paths.get(uploadDir, fileName);
    }

    @Override
    public byte[] getFileBytes(String filePath) throws IOException {
        Path path = getFilePath(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }
        return Files.readAllBytes(path);
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        return UUID.randomUUID().toString() + "." + extension;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }

    private void validateFileType(MultipartFile file, String expectedType) {
        String extension = getFileExtension(file.getOriginalFilename());
        if (!extension.equals(expectedType)) {
            throw new IllegalArgumentException("File type not supported. Expected: " + expectedType);
        }
    }

    private void validateImageType(MultipartFile file) {
        String extension = getFileExtension(file.getOriginalFilename());
        if (!extension.equals("jpg") && !extension.equals("jpeg") && 
            !extension.equals("png") && !extension.equals("gif")) {
            throw new IllegalArgumentException("Image type not supported. Supported types: jpg, jpeg, png, gif");
        }
    }
}
