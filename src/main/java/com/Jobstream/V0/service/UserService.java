package com.Jobstream.V0.service;

import com.Jobstream.V0.dto.response.UserResponse;
import com.Jobstream.V0.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserService {

    UserResponse getById(UUID id);

    Page<UserResponse> searchUsers(String query, Pageable pageable);

    UserResponse getCurrentUser(String email);

    void deleteUser(UUID id, String currentUserEmail);

    UserResponse updateRole(UUID id, Role role);
}
