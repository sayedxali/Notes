package com.example.springswagger2.config.jwt.filter;

import com.example.springswagger2.config.jwt.JWTService;
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
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Value("${filter.token.prefix}")
    private String TOKEN_PREFIX;

    @Value("${filter.paths.exclude.url}")
    private List<String> EXCLUDED_PATHS;

    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (excludePaths(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            log.error("❌❌❌ Invalid token or header = null ❌❌❌");
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
            throw new IllegalArgumentException("Invalid token request ❌");
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.validateToken(token, userDetails)) {
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
            throw new IllegalArgumentException("Invalid login Request! user not valid! ❌❌❌");
        }
        log.info("JWT token is valid! ✅");
        filterChain.doFilter(request, response);
    }


    private boolean excludePaths(HttpServletRequest request) {
//        String path = request.getRequestURL().toString();
        String path = request.getRequestURI();
        return EXCLUDED_PATHS
                .stream()
                .noneMatch(pathMatches -> pathMatches.contains(path));
    }

}
