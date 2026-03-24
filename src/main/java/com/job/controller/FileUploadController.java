package com.job.controller;

import com.job.service.FileUploadService;
import com.job.service.UserService;
import com.job.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    private final FileUploadService fileUploadService;
    private final AuthUtil authUtil;
    private final UserService userService;
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("directory") String directory) {
        try {
            String filePath = fileUploadService.uploadFile(file, directory);
            return ResponseEntity.ok(filePath);
        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage());
            return ResponseEntity.badRequest().body("File upload failed: " + e.getMessage());
        }
    }

    @PostMapping("/upload-cv")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<String> uploadCV(
            @RequestParam("file") MultipartFile file) {
        try {
            Long userId = authUtil.getCurrentUserId();
            String filePath = fileUploadService.uploadCV(file, userId);
            return ResponseEntity.ok(filePath);
        } catch (IOException e) {
            log.error("Error uploading CV for current user: {}", e.getMessage());
            return ResponseEntity.badRequest().body("CV upload failed: " + e.getMessage());
        }
    }

    @PostMapping("/upload-profile-picture")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<String> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        try {
            Long userId = authUtil.getCurrentUserId();
            String filePath = userService.updateProfilePicture(userId, file);
            return ResponseEntity.ok(filePath);
        } catch (IOException e) {
            log.error("Error uploading profile picture: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Profile picture upload failed: " + e.getMessage());
        }
    }
    @PostMapping("/upload-company-logo")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<String> uploadCompanyLogo(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long companyId) {
        try {
            String filePath = fileUploadService.uploadCompanyLogo(file, companyId);
            return ResponseEntity.ok(filePath);
        } catch (IOException e) {
            log.error("Error uploading company logo for company {}: {}", companyId, e.getMessage());
            return ResponseEntity.badRequest().body("Company logo upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/download/**")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filePath) {
        try {
            byte[] fileBytes = fileUploadService.getFileBytes(filePath);
            ByteArrayResource resource = new ByteArrayResource(fileBytes);
            
            String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
            String contentType = determineContentType(fileName);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (IOException e) {
            log.error("Error downloading file {}: {}", filePath, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/view/**")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Resource> viewFile(@RequestParam String filePath) {
        try {
            byte[] fileBytes = fileUploadService.getFileBytes(filePath);
            ByteArrayResource resource = new ByteArrayResource(fileBytes);
            
            String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
            String contentType = determineContentType(fileName);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (IOException e) {
            log.error("Error viewing file {}: {}", filePath, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> deleteFile(@RequestParam String filePath) {
        try {
            fileUploadService.deleteFile(filePath);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            log.error("Error deleting file {}: {}", filePath, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/exists")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Boolean> fileExists(@RequestParam String filePath) {
        boolean exists = fileUploadService.fileExists(filePath);
        return ResponseEntity.ok(exists);
    }

    private String determineContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "txt" -> "text/plain";
            case "doc", "docx" -> "application/msword";
            case "xls", "xlsx" -> "application/vnd.ms-excel";
            default -> "application/octet-stream";
        };
    }
}
