package com.Jobstream.V0.service;

import com.Jobstream.V0.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserBlockService {

    void blockUser(UUID blockerId, UUID blockedId);

    void unblockUser(UUID blockerId, UUID blockedId);

    boolean isBlocked(UUID blockerId, UUID blockedId);

    List<UserResponse> getBlockedUsers(UUID userId);
}
