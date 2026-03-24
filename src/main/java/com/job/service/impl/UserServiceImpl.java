package com.job.service.impl;

import com.job.dto.request.UserCreateRequestDTO;
import com.job.dto.response.UserResponseDTO;
import com.job.entity.User;
import com.job.exception.UserNotFoundException;
import com.job.mapper.UserMapper;
import com.job.repository.UserRepository;
import com.job.service.FileUploadService;
import com.job.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadService fileUploadService;

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

    @Override
    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponseDTO getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email " + email));
        return userMapper.toResponse(user);
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public String updateProfilePicture(Long userId, MultipartFile file) throws IOException {
        String filePath = fileUploadService.uploadProfilePicture(file, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        user.setProfilePicture(filePath);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return filePath;
    }

    @Override
    public UserResponseDTO updateUser(Long userId, com.job.dto.request.UserUpdateRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        if (dto.getFirstName() != null)
            user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null)
            user.setLastName(dto.getLastName());
        if (dto.getPhone() != null)
            user.setPhone(dto.getPhone());
        if (dto.getBio() != null)
            user.setBio(dto.getBio());
        if (dto.getLocation() != null)
            user.setLocation(dto.getLocation());
        if (dto.getWebsite() != null)
            user.setWebsite(dto.getWebsite());
        if (dto.getLinkedinProfile() != null)
            user.setLinkedinProfile(dto.getLinkedinProfile());

        user.setUpdatedAt(LocalDateTime.now());

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public java.util.List<com.job.dto.response.UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .toList();
    }
}
