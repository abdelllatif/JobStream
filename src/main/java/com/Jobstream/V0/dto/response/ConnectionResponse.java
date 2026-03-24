package com.Jobstream.V0.dto.response;

import com.Jobstream.V0.enums.ConnectionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ConnectionResponse {

    private UUID id;
    private UUID senderId;
    private String senderEmail;
    private String senderHeadline;
    private String senderPhotoUrl;
    private UUID receiverId;
    private String receiverEmail;
    private String receiverHeadline;
    private String receiverPhotoUrl;
    private ConnectionStatus status;
    private LocalDateTime createdAt;
}
