package com.job.websocket;

import com.job.dto.request.MessageCreateRequest;
import com.job.dto.response.MessageResponseDTO;
import com.job.entity.Message;
import com.job.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageCreateRequest request) {
        Message message = (request.getJobId() != null)
                ? messageService.sendMessage(request.getSenderId(), request.getReceiverId(),
                        request.getContent(), request.getJobId())
                : messageService.sendMessage(request.getSenderId(), request.getReceiverId(),
                        request.getContent());

        MessageResponseDTO dto = MessageResponseDTO.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .receiverId(message.getReceiver().getId())
                .content(message.getContent())
                .jobId(message.getJob() != null ? message.getJob().getId() : null)
                .createdAt(message.getSentAt() != null
                        ? message.getSentAt().toString()
                        : (message.getCreatedAt() != null ? message.getCreatedAt().toString() : null))
                .read(message.isRead())
                .build();

        String receiverTopic = "/topic/messages." + message.getReceiver().getId();
        String senderTopic = "/topic/messages." + message.getSender().getId();

        messagingTemplate.convertAndSend(receiverTopic, dto);
        if (!receiverTopic.equals(senderTopic)) {
            messagingTemplate.convertAndSend(senderTopic, dto);
        }

        log.info("Sent chat message from {} to {} over STOMP", message.getSender().getId(),
                message.getReceiver().getId());
    }
}

