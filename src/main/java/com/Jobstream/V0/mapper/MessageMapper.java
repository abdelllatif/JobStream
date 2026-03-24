package com.Jobstream.V0.mapper;

import com.Jobstream.V0.dto.response.MessageResponse;
import com.Jobstream.V0.entity.Message;

public class MessageMapper {

    public static MessageResponse toResponse(Message message) {
        String senderPhotoUrl = message.getSender().getProfile() != null
                ? message.getSender().getProfile().getPhotoUrl() : null;

        return MessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .senderEmail(message.getSender().getEmail())
                .senderPhotoUrl(senderPhotoUrl)
                .content(message.getContent())
                .jobId(message.getJob() != null ? message.getJob().getId() : null)
                .jobTitle(message.getJob() != null ? message.getJob().getTitle() : null)
                .isRead(message.isRead())
                .createdAt(message.getCreatedAt())
                .build();
    }

    private MessageMapper() {}
}
