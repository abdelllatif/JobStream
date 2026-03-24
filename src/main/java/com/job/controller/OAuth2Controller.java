package com.job.controller;

import com.job.dto.response.AuthResponse;
import com.job.entity.User;
import com.job.security.OAuth2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
@Slf4j
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;

    @GetMapping("/success")
    public ResponseEntity<AuthResponse> oauth2Success(@AuthenticationPrincipal OAuth2User oAuth2User) {
        try {
            User user = oAuth2Service.processOAuth2User(oAuth2User);
            String token = oAuth2Service.generateJwtToken(user);
            
            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setUserId(user.getId());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole().name());
            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());
            response.setProfilePicture(user.getProfilePicture());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing OAuth2 success", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/failure")
    public ResponseEntity<Map<String, String>> oauth2Failure() {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "OAuth2 authentication failed",
                "message", "Unable to authenticate with Google"
        ));
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(oAuth2User.getAttributes());
    }
}
