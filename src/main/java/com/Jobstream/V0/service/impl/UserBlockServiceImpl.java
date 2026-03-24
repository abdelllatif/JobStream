package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.response.UserResponse;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.entity.UserBlock;
import com.Jobstream.V0.exception.BadRequestException;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.mapper.UserMapper;
import com.Jobstream.V0.repository.UserBlockRepository;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.UserBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserBlockServiceImpl implements UserBlockService {

    private final UserBlockRepository userBlockRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void blockUser(UUID blockerId, UUID blockedId) {
        if (blockerId.equals(blockedId)) {
            throw new BadRequestException("Cannot block yourself");
        }
        if (userBlockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
            throw new BadRequestException("User is already blocked");
        }
        User blocker = findUser(blockerId);
        User blocked = findUser(blockedId);
        UserBlock block = UserBlock.builder().blocker(blocker).blocked(blocked).build();
        userBlockRepository.save(block);
    }

    @Override
    @Transactional
    public void unblockUser(UUID blockerId, UUID blockedId) {
        if (!userBlockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
            throw new ResourceNotFoundException("Block not found");
        }
        userBlockRepository.deleteByBlockerIdAndBlockedId(blockerId, blockedId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBlocked(UUID blockerId, UUID blockedId) {
        return userBlockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getBlockedUsers(UUID userId) {
        return userBlockRepository.findByBlockerId(userId)
                .stream()
                .map(block -> UserMapper.toResponse(block.getBlocked()))
                .collect(Collectors.toList());
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
