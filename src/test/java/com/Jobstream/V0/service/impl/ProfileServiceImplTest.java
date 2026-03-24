package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.request.ProfileRequest;
import com.Jobstream.V0.dto.response.ProfileResponse;
import com.Jobstream.V0.entity.Profile;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.repository.ProfileRepository;
import com.Jobstream.V0.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Test
    void getProfileByUserId_Success() {
        UUID userId = UUID.randomUUID();
        Profile profile = Profile.builder()
                .id(UUID.randomUUID())
                .headline("Software Engineer")
                .user(User.builder().id(userId).build())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        ProfileResponse response = profileService.getByUserId(userId);

        assertNotNull(response);
        assertEquals("Software Engineer", response.getHeadline());
    }

    @Test
    void createOrUpdateProfile_CreatesNew() {
        UUID userId = UUID.randomUUID();
        ProfileRequest request = new ProfileRequest();
        request.setHeadline("New Headline");

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(profileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(profileRepository.save(any(Profile.class))).thenAnswer(i -> i.getArguments()[0]);

        ProfileResponse response = profileService.createOrUpdate(userId, request);

        assertNotNull(response);
        assertEquals("New Headline", response.getHeadline());
        verify(profileRepository).save(any(Profile.class));
    }
}
