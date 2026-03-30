package com.Jobstream.V0.mapper;

import com.Jobstream.V0.dto.response.ProfileResponse;
import com.Jobstream.V0.dto.response.UserResponse;
import com.Jobstream.V0.entity.Connection;
import com.Jobstream.V0.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        return toResponse(user, List.of());
    }

    public static UserResponse toResponse(User user, List<Connection> connections) {
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
                .connections(connections.stream().map(ConnectionMapper::toResponse).collect(Collectors.toList()))
                .build();
    }

    private UserMapper() {}
}
