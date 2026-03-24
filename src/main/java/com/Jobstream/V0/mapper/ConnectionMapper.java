package com.Jobstream.V0.mapper;

import com.Jobstream.V0.dto.response.ConnectionResponse;
import com.Jobstream.V0.entity.Connection;
import com.Jobstream.V0.entity.User;

public class ConnectionMapper {

    public static ConnectionResponse toResponse(Connection connection) {
        String senderHeadline = headlineOf(connection.getSender());
        String senderPhoto = photoOf(connection.getSender());
        String receiverHeadline = headlineOf(connection.getReceiver());
        String receiverPhoto = photoOf(connection.getReceiver());

        return ConnectionResponse.builder()
                .id(connection.getId())
                .senderId(connection.getSender().getId())
                .senderEmail(connection.getSender().getEmail())
                .senderHeadline(senderHeadline)
                .senderPhotoUrl(senderPhoto)
                .receiverId(connection.getReceiver().getId())
                .receiverEmail(connection.getReceiver().getEmail())
                .receiverHeadline(receiverHeadline)
                .receiverPhotoUrl(receiverPhoto)
                .status(connection.getStatus())
                .createdAt(connection.getCreatedAt())
                .build();
    }

    private static String headlineOf(User user) {
        return user.getProfile() != null ? user.getProfile().getHeadline() : null;
    }

    private static String photoOf(User user) {
        return user.getProfile() != null ? user.getProfile().getPhotoUrl() : null;
    }

    private ConnectionMapper() {}
}
