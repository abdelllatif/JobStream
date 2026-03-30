package com.Jobstream.V0.controller;

import com.Jobstream.V0.dto.request.ConversationRequest;
import com.Jobstream.V0.dto.response.ConversationResponse;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Conversations", description = "Endpoints for managing messaging conversations")
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping("/my")
    @Operation(summary = "Get all conversations for the current user")
    public ResponseEntity<List<ConversationResponse>> getMyConversations(Authentication auth) {
        return ResponseEntity.ok(conversationService.getMyConversations(currentUserId(auth)));
    }

    @PostMapping("/find-or-create")
    @Operation(summary = "Find existing or create new direct conversation")
    public ResponseEntity<ConversationResponse> findOrCreate(
            @Valid @RequestBody ConversationRequest request, Authentication auth) {
        return ResponseEntity.ok(conversationService.findOrCreateDirectConversation(currentUserId(auth), request));
    }

    private static UUID currentUserId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }
}
