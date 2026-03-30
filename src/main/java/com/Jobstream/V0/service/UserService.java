package com.Jobstream.V0.service;

import com.Jobstream.V0.dto.request.SetPasswordRequest;
import com.Jobstream.V0.dto.response.UserResponse;
import com.Jobstream.V0.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserResponse getById(UUID id);

    Page<UserResponse> searchUsers(String query, String currentUserEmail, Pageable pageable);

    UserResponse getCurrentUser(String email);

    void disableUser(UUID id, String currentUserEmail);

    void activateUser(UUID id);

    UserResponse updateRole(UUID id, Role role);

    Page<UserResponse> getNetworkUsers(String currentUserEmail, Pageable pageable);

    Page<UserResponse> getAllUsersExcludingAdmins(Pageable pageable);

    void changePassword(String currentUserEmail, com.Jobstream.V0.dto.request.ChangePasswordRequest request);

    boolean hasPassword(UUID userId);

    void setPassword(UUID userId, SetPasswordRequest request);
}
