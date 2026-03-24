package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.request.RegisterRequest;
import com.Jobstream.V0.dto.response.UserResponse;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.exception.DuplicateResourceException;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserById_Success() {
        UUID id = UUID.randomUUID();
        User user = User.builder().id(id).email("test@example.com").build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserResponse response = userService.getById(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void getUserById_ThrowsResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getById(id));
    }

    @Test
    void updateRole_Success() {
        UUID id = UUID.randomUUID();
        User user = User.builder().id(id).role(com.Jobstream.V0.enums.Role.USER).build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.updateRole(id, com.Jobstream.V0.enums.Role.ADMIN);

        assertNotNull(response);
        assertEquals(com.Jobstream.V0.enums.Role.ADMIN, response.getRole());
        verify(userRepository).save(user);
    }
}
