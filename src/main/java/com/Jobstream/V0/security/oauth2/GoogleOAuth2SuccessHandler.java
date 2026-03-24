package com.Jobstream.V0.security.oauth2;

import com.Jobstream.V0.security.JwtService;
import com.Jobstream.V0.entity.User;
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
                .orElseGet(() -> createOAuth2User(email, providerId, name, picture));

        if (user.getProvider() == Provider.LOCAL) {
            user.setProviderId(providerId);
            userRepository.save(user);
        }

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("token", token)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        log.info("OAuth2 login successful for: {}", email);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private User createOAuth2User(String email, String providerId, String name, String picture) {
        User newUser = User.builder()
                .email(email)
                .provider(Provider.GOOGLE)
                .providerId(providerId)
                .role(Role.USER)
                .enabled(true)
                .build();
        return userRepository.save(newUser);
    }
}
