package com.job.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileUploadService {
    String uploadFile(MultipartFile file, String directory) throws IOException;
    String uploadCV(MultipartFile file, Long userId) throws IOException;
    String uploadProfilePicture(MultipartFile file, Long userId) throws IOException;
    String uploadCompanyLogo(MultipartFile file, Long companyId) throws IOException;
    void deleteFile(String filePath) throws IOException;
    boolean fileExists(String filePath);
    Path getFilePath(String fileName);
    byte[] getFileBytes(String filePath) throws IOException;
}
