package com.Jobstream.V0.mapper;

import com.Jobstream.V0.dto.response.ProfileResponse;
import com.Jobstream.V0.dto.response.UserResponse;
import com.Jobstream.V0.entity.User;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .provider(user.getProvider())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .profile(user.getProfile() != null ? ProfileMapper.toResponse(user.getProfile()) : null)
                .build();
    }

    private UserMapper() {}
}
