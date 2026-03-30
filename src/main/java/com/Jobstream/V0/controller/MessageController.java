package com.Jobstream.V0.controller;

import com.Jobstream.V0.dto.request.MessageRequest;
import com.Jobstream.V0.dto.response.MessageResponse;
import com.Jobstream.V0.dto.response.PageResponse;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Messages", description = "Endpoints for direct messaging")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @Operation(summary = "Send a new message via REST")
    public ResponseEntity<MessageResponse> sendMessage(
            @Valid @RequestBody MessageRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.sendMessage(currentUserId(auth), request));
    }

    @GetMapping("/{conversationId}")
    @Operation(summary = "Get messages for a conversation")
    public ResponseEntity<PageResponse<MessageResponse>> getMessages(
            @PathVariable UUID conversationId, Pageable pageable, Authentication auth) {
        return ResponseEntity.ok(messageService.getMessages(conversationId, currentUserId(auth), pageable));
    }

    @PutMapping("/read/{conversationId}")
    @Operation(summary = "Mark all messages in a conversation as read")
    public ResponseEntity<Integer> markConversationAsRead(
            @PathVariable UUID conversationId, Authentication auth) {
        return ResponseEntity.ok(messageService.markConversationAsRead(conversationId, currentUserId(auth)));
    }

    private static UUID currentUserId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }
}
