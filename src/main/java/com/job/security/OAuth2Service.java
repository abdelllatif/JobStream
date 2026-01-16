package com.job.security;

import com.job.entity.User;
import com.job.enums.Role;
import com.job.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2Service {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public User processOAuth2User(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String firstName = (String) attributes.get("given_name");
        String lastName = (String) attributes.get("family_name");
        String picture = (String) attributes.get("picture");
        String googleId = (String) attributes.get("sub");

        log.info("Processing OAuth2 user with email: {}", email);

        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            if (existingUser.getGoogleId() == null) {
                existingUser.setGoogleId(googleId);
                existingUser.setProfilePicture(picture);
                existingUser.setEmailVerified(true);
                existingUser.setUpdatedAt(LocalDateTime.now());
                return userRepository.save(existingUser);
            }
            return existingUser;
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(firstName != null ? firstName : "");
            newUser.setLastName(lastName != null ? lastName : "");
            newUser.setGoogleId(googleId);
            newUser.setProfilePicture(picture);
            newUser.setEmailVerified(true);
            newUser.setRole(Role.CANDIDATE);
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedAt(LocalDateTime.now());
            
            return userRepository.save(newUser);
        }
    }

    public String generateJwtToken(User user) {
        return jwtService.generateToken(user.getEmail(), user.getId());
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            String email = ((org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal()).getUsername();
            return userRepository.findByEmail(email).orElse(null);
        }
        return null;
    }
}
