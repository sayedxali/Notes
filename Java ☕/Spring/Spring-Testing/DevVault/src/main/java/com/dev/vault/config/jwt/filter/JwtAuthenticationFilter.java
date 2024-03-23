package com.dev.vault.config.jwt.filter;

import com.dev.vault.config.jwt.JwtService;
import com.dev.vault.helper.exception.DevVaultException;
import com.dev.vault.repository.user.jwt.JwtTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenRepository jwtTokenRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private @Value("${filter.token.prefix}") String TOKEN_PREFIX;

    private final List<String> EXCLUDED_PATHS = List.of(
            "http://localhost:8080/swagger-ui.html",
            "http://localhost:8080/swagger-ui/index.html",
            "http://localhost:8080/api/v1/auth",
            "http://localhost:8080/swagger-ui/swagger-initializer.js",
            "http://localhost:8080/v3/api-docs/swagger-config",
            "http://localhost:8080/v3/api-docs"
    );

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            log.error("❌❌❌ AuthHeader == null || Token == Invalid ❌❌❌");
            filterChain.doFilter(request, response);
            return;
        }
        log.info("header is valid ✅ ...");
        token = authHeader.substring(TOKEN_PREFIX.length()); // which is 7

        try {
            userEmail = jwtService.extractUsername(token);
            log.info("username extracted :: " + userEmail);
        } catch (ExpiredJwtException e) {
            log.error("JWT token has expired ❌");
            throw new ExpiredJwtException(null, null, "JWT token has been expired ❌", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid token request ❌");
            throw new DevVaultException("Invalid token request ❌");
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            Boolean isTokenValid = jwtTokenRepository.findByToken(token)
                    .map(jwtToken -> !jwtToken.isRevoked() && !jwtToken.isExpired())
                    .orElse(false);
            if (jwtService.validateToken(token, userDetails) && isTokenValid) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } else {
            log.error("Invalid login Request! user not valid! ❌❌❌");
            throw new DevVaultException("Invalid login Request! user not valid! ❌❌❌");
        }
        log.info("JWT token is valid! ✅");
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/auth/**") || this.EXCLUDED_PATHS.stream().anyMatch(excludePath -> true);
    }

}
