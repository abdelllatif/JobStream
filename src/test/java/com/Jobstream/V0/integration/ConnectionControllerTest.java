package com.Jobstream.V0.integration;

import com.Jobstream.V0.dto.request.ConnectionRequest;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.enums.ConnectionStatus;
import com.Jobstream.V0.enums.Provider;
import com.Jobstream.V0.enums.Role;
import com.Jobstream.V0.repository.ConnectionRepository;
import com.Jobstream.V0.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConnectionControllerTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConnectionRepository connectionRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        connectionRepository.deleteAll();
        userRepository.deleteAll();

        user1 = userRepository.save(User.builder()
                .email("user1@example.com")
                .password("encoded")
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build());

        user2 = userRepository.save(User.builder()
                .email("user2@example.com")
                .password("encoded")
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Test
    @WithMockUser(username = "user1@example.com", roles = "USER")
    void shouldSendConnectionRequest() throws Exception {
        ConnectionRequest request = new ConnectionRequest();
        request.setReceiverId(user2.getId());

        ResultActions response = mockMvc.perform(post("/api/connections/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(ConnectionStatus.PENDING.name()))
                .andExpect(jsonPath("$.sender.id").value(user1.getId().toString()))
                .andExpect(jsonPath("$.receiver.id").value(user2.getId().toString()));
    }

    @Test
    @WithMockUser(username = "user2@example.com", roles = "USER")
    void shouldAcceptConnectionRequest() throws Exception {
        // user1 sends request to user2
        com.Jobstream.V0.entity.Connection connection = com.Jobstream.V0.entity.Connection.builder()
                .sender(user1)
                .receiver(user2)
                .status(ConnectionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        connection = connectionRepository.save(connection);

        // user2 accepts
        ResultActions response = mockMvc.perform(put("/api/connections/" + connection.getId() + "/accept")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ConnectionStatus.ACCEPTED.name()));
    }
}
