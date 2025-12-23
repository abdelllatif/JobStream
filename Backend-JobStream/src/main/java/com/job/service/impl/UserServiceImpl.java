package com.job.service.impl;

import com.job.dto.request.UserCreateRequestDTO;
import com.job.dto.response.UserResponseDTO;
import com.job.entity.User;
import com.job.mapper.UserMapper;
import com.job.repository.UserRepository;
import com.job.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO register(UserCreateRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setCandidateProfile(null);
        return userMapper.toResponse(userRepository.save(user));
    }
}
