package com.example.logindemo.handler;

import com.example.logindemo.service.CookieService;
import com.example.logindemo.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Runs after a successful GitHub OAuth login.
 * It mints OUR OWN JWT for the GitHub user and drops it in the same httpOnly cookie
 * that email/password login uses - so the rest of the app treats both logins identically.
 * Then it redirects the browser back to the frontend.
 */
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final CookieService cookieService;
    private final String frontendUrl;

    public OAuth2LoginSuccessHandler(
            JwtService jwtService,
            CookieService cookieService,
            @Value("${app.frontend-url}") String frontendUrl
    ) {
        this.jwtService = jwtService;
        this.cookieService = cookieService;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        String username = user.getAttribute("login");


        ResponseCookie accessCookie = cookieService.createTokenCookie(jwtService.generateToken(username));
        ResponseCookie refreshCookie = cookieService.createRefreshCookie(jwtService.generateRefreshToken(username));


        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());


        getRedirectStrategy().sendRedirect(request, response, frontendUrl);
    }
}
