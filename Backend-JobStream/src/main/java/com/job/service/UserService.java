package com.job.service;

import com.job.dto.request.UserCreateRequestDTO;
import com.job.dto.response.UserResponseDTO;

public interface UserService {
    UserResponseDTO register(UserCreateRequestDTO dto);
    UserResponseDTO getById(Long id);
    UserResponseDTO getByEmail(String email);
    boolean existsById(Long id);
    boolean existsByEmail(String email);
}
