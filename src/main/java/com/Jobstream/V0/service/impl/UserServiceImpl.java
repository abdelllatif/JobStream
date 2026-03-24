package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.response.UserResponse;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.enums.Role;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.exception.UnauthorizedException;
import com.Jobstream.V0.mapper.UserMapper;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        User user = findUserById(id);
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String query, Pageable pageable) {
        return userRepository.searchUsers(query, pageable)
                .map(UserMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id, String currentUserEmail) {
        User requester = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User target = findUserById(id);
        if (!requester.getId().equals(target.getId()) &&
                !requester.getRole().equals(Role.ADMIN)) {
            throw new UnauthorizedException("Not authorized to delete this user");
        }
        userRepository.delete(target);
    }

    @Override
    @Transactional
    public UserResponse updateRole(UUID id, Role role) {
        User user = findUserById(id);
        user.setRole(role);
        userRepository.save(user);
        return UserMapper.toResponse(user);
    }

    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
