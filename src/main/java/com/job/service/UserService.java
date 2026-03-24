package com.job.service;

import com.job.dto.request.UserCreateRequestDTO;
import com.job.dto.response.UserResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    UserResponseDTO register(UserCreateRequestDTO dto);

    UserResponseDTO getById(Long id);

    UserResponseDTO getByEmail(String email);

    boolean existsById(Long id);

    boolean existsByEmail(String email);

    String updateProfilePicture(Long userId, MultipartFile file) throws IOException;

    UserResponseDTO updateUser(Long userId, com.job.dto.request.UserUpdateRequest dto);

    java.util.List<com.job.dto.response.UserDTO> getAllUsers();
}
