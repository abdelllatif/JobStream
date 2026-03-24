package com.Jobstream.V0.integration;

import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.enums.Provider;
import com.Jobstream.V0.enums.Role;
import com.Jobstream.V0.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User savedUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = User.builder()
                .email("user1@example.com")
                .password(passwordEncoder.encode("Password123!"))
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();
        
        savedUser = userRepository.save(user);

        User user2 = User.builder()
                .email("user2@example.com")
                .password(passwordEncoder.encode("Password123!"))
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user2);
    }

    @Test
    @WithMockUser(username = "user1@example.com", roles = "USER")
    void shouldGetCurrentUser() throws Exception {
        ResultActions response = mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user1@example.com"));
    }

    @Test
    @WithMockUser(username = "user1@example.com", roles = "USER")
    void shouldGetUserById() throws Exception {
        ResultActions response = mockMvc.perform(get("/api/users/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user1@example.com"));
    }

    @Test
    void shouldFailIfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()); // 401
    }
}
