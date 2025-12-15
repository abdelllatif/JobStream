package com.job.service;

import com.job.dto.request.UserRequestDTO;
import com.job.dto.response.UserResponseDTO;

public interface UserService {
    UserResponseDTO register(UserRequestDTO dto);
}
