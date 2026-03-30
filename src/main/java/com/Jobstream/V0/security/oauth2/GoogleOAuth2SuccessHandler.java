package com.Jobstream.V0.security.oauth2;

import com.Jobstream.V0.security.JwtService;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.entity.Profile;
import com.Jobstream.V0.enums.Provider;
import com.Jobstream.V0.enums.Role;
import com.Jobstream.V0.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Value("${application.oauth2.redirect-url}")
    private String redirectUrl;

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String providerId = oAuth2User.getAttribute("sub");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        User user = userRepository.findByEmail(email)
                .map(existing -> linkGoogleToExistingUser(existing, providerId))
                .orElseGet(() -> createOAuth2User(email, providerId, name, picture));

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("token", token)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        log.info("OAuth2 login successful for: {} (provider: {})", email, user.getProvider());
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * Called when a Google login arrives for an email that already exists in the DB.
     * Scenarios:
     *  - provider=LOCAL      → link Google → set providerId + provider=LOCAL_GOOGLE
     *  - provider=GOOGLE     → already Google user, just update providerId if changed
     *  - provider=LOCAL_GOOGLE → already linked, nothing to change
     */
    private User linkGoogleToExistingUser(User existing, String providerId) {
        boolean needsSave = false;

        if (existing.getProvider() == Provider.LOCAL) {
            existing.setProvider(Provider.LOCAL_GOOGLE);
            existing.setProviderId(providerId);
            needsSave = true;
            log.info("Linked Google to existing LOCAL account: {}", existing.getEmail());
        } else if (existing.getProvider() == Provider.GOOGLE
                && !providerId.equals(existing.getProviderId())) {
            existing.setProviderId(providerId);
            needsSave = true;
        }

        return needsSave ? userRepository.save(existing) : existing;
    }

    private User createOAuth2User(String email, String providerId, String name, String picture) {
        String firstName = "";
        String lastName = "";

        if (name != null && !name.isBlank()) {
            String[] parts = name.split("\\s+", 2);
            firstName = parts[0];
            lastName = (parts.length > 1) ? parts[1] : "";
        }

        User newUser = User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .provider(Provider.GOOGLE)
                .providerId(providerId)
                .role(Role.USER)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(newUser);

        // Initialize empty profile
        Profile profile = Profile.builder()
                .user(savedUser)
                .build();
        savedUser.setProfile(profile);

        return userRepository.save(savedUser);
    }
}
