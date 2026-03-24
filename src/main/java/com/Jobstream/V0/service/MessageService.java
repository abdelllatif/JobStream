package com.Jobstream.V0.service;

import com.Jobstream.V0.dto.request.MessageRequest;
import com.Jobstream.V0.dto.response.MessageResponse;
import com.Jobstream.V0.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MessageService {

    MessageResponse sendMessage(UUID senderId, MessageRequest request);

    PageResponse<MessageResponse> getMessages(UUID conversationId, UUID userId, Pageable pageable);

    int markConversationAsRead(UUID conversationId, UUID userId);
}
