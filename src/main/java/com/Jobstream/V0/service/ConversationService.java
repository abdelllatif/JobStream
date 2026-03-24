package com.Jobstream.V0.service;

import com.Jobstream.V0.dto.request.ConversationRequest;
import com.Jobstream.V0.dto.response.ConversationResponse;

import java.util.List;
import java.util.UUID;

public interface ConversationService {

    List<ConversationResponse> getMyConversations(UUID userId);

    ConversationResponse findOrCreateDirectConversation(UUID userId, ConversationRequest request);

    ConversationResponse getById(UUID conversationId, UUID userId);
}
