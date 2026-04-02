package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.request.ChangePasswordRequest;
import com.Jobstream.V0.dto.request.SetPasswordRequest;
import com.Jobstream.V0.dto.response.UserResponse;
import com.Jobstream.V0.entity.Connection;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.enums.Provider;
import com.Jobstream.V0.enums.Role;
import com.Jobstream.V0.exception.BadRequestException;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.exception.UnauthorizedException;
import com.Jobstream.V0.exception.UserSuspendException;
import com.Jobstream.V0.mapper.UserMapper;
import com.Jobstream.V0.repository.ConnectionRepository;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ConnectionRepository connectionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        User user = findUserById(id);
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String query, String currentUserEmail, Pageable pageable) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Page<User> usersPage = userRepository.searchUsers(query, currentUser.getId(), pageable);
        List<UUID> userIds = usersPage.getContent().stream().map(User::getId).collect(Collectors.toList());
        List<Connection> connections = userIds.isEmpty() ? List.of()
                : connectionRepository.findConnectionsByUsers(userIds);
        final UUID currentUserId = currentUser.getId();
        Map<UUID, List<Connection>> connectionsMap = connections.stream()
                .filter(c -> c.getSender().getId().equals(currentUserId) || c.getReceiver().getId().equals(currentUserId))
                .collect(Collectors.groupingBy(c ->
                        c.getSender().getId().equals(currentUserId)
                                ? c.getReceiver().getId()
                                : c.getSender().getId()
                ));
        return usersPage.map(user -> UserMapper.toResponse(user, connectionsMap.getOrDefault(user.getId(), List.of())));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void activateUser(UUID id) {
        User user = findUserById(id);
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void disableUser(UUID id, String currentUserEmail) {
        User requester = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User target = findUserById(id);
        if (!requester.getId().equals(target.getId()) &&
                !requester.getRole().equals(Role.ADMIN)) {
            throw new UnauthorizedException("Not authorized to disable this user");
        }
        target.setEnabled(false);
        userRepository.save(target);
    }

    @Override
    @Transactional
    public UserResponse updateRole(UUID id, Role role) {
        User user = findUserById(id);
        user.setRole(role);
        userRepository.save(user);
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getNetworkUsers(String currentUserEmail, Pageable pageable) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Page<User> usersPage = userRepository.findNetworkUsers(currentUser.getId(), pageable);
        List<UUID> userIds = usersPage.getContent().stream().map(User::getId).collect(Collectors.toList());
        List<Connection> allConnections = connectionRepository.findConnectionsByUsers(userIds);
        Map<UUID, List<Connection>> connectionsMap = allConnections.stream()
                .flatMap(c -> List.of(
                        Map.entry(c.getSender().getId(), c),
                        Map.entry(c.getReceiver().getId(), c)
                ).stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
        return usersPage.map(user -> UserMapper.toResponse(user, connectionsMap.getOrDefault(user.getId(), List.of())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsersExcludingAdmins(Pageable pageable) {
        return userRepository.findAllExcludingAdmins(pageable)
                .map(UserMapper::toResponse);
    }

    @Override
    @Transactional
    public void changePassword(String currentUserEmail, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmedPassword())) {
            throw new BadRequestException("New password and confirmed password do not match");
        }
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new BadRequestException("This account uses Google login. Please login via Google.");
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPassword(UUID userId) {
        User user = findUserById(userId);
        return user.getPassword() != null && !user.getPassword().isBlank();
    }

    @Override
    @Transactional
    public void setPassword(UUID userId, SetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmedPassword())) {
            throw new BadRequestException("New password and confirmed password do not match");
        }
        User user = findUserById(userId);
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            throw new BadRequestException("You already have a password. Use change password instead.");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        if (user.getProvider() == Provider.GOOGLE) {
            user.setProvider(Provider.LOCAL_GOOGLE);
        }
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public void userIsSuspend(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (!user.isEnabled()) {
                throw new UserSuspendException("Vous etes suspendu. Les admins ont vu quelque chose de suspect. Votre activite et vos applications sont encore actives. Veuillez attendre maximum 1 jour pour retirer la suspension.");
            }
        });
    }
}
