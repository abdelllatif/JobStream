package com.Jobstream.V0.controller;

import com.Jobstream.V0.dto.request.MessageRequest;
import com.Jobstream.V0.dto.response.MessageResponse;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    /**
     * STOMP Message Mapping for sending a real-time message.
     * Clients should send payload to "/app/chat.send"
     */
    @MessageMapping("/chat.send")
    public void handleChatMessage(@Payload MessageRequest request, Authentication authentication) {
        try {
            if (authentication == null) {
                log.error("Unauthenticated user tried to send a message via WS");
                return;
            }

            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + authentication.getName()));

            // Service internal handles the saving and the broadcast to /queue/messages/{id}
            MessageResponse response = messageService.sendMessage(user.getId(), request);
            
            log.info("WebSocket message processed and broadcasted for conversation {}", request.getConversationId());
        } catch (Exception e) {
            log.error("Error processing websocket message: {}", e.getMessage());
        }
    }
}
