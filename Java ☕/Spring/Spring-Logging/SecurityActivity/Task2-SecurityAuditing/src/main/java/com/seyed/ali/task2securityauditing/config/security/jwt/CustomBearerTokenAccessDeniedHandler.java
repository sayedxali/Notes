package com.seyed.ali.task2securityauditing.config.security.jwt;

import com.seyed.ali.task2securityauditing.model.entity.UserActivity;
import com.seyed.ali.task2securityauditing.repository.UserActivityRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Map;

import static com.seyed.ali.task2securityauditing.model.enums.ActivityType.ACCESS_DENIED;

/**
 * This class handles unsuccessful JWT authorization.
 */
@Slf4j
@Component
public class CustomBearerTokenAccessDeniedHandler implements AccessDeniedHandler {

    private final HandlerExceptionResolver resolver;
    private final UserActivityRepository userActivityRepository;
    private final JwtDecoder jwtDecoder;

    @Autowired
    public CustomBearerTokenAccessDeniedHandler(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
            UserActivityRepository userActivityRepository, JwtDecoder jwtDecoder
    ) {
        this.resolver = resolver;
        this.userActivityRepository = userActivityRepository;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        String remoteAddr = request.getRemoteAddr();
        String sessionId = request.getSession().getId();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken) {
            String token = ((JwtAuthenticationToken) authentication).getToken().getTokenValue();
            Map<String, Object> jwtClaims = this.jwtDecoder.decode(token).getClaims();
            String username = (String) jwtClaims.get("sub");
            long userId = (long) jwtClaims.get("userId");

            log.warn("Access Denied: Principal '{}' - '{}'", username, ACCESS_DENIED);
            log.warn("Remote IP address: {}", remoteAddr);
            log.warn("Session Id: {}", sessionId);

            UserActivity userActivity = UserActivity.builder()
                    .createdBy(userId)
                    .createdByName(username)
                    .ipAddress(remoteAddr)
                    .activityType(ACCESS_DENIED)
                    .build();

            this.userActivityRepository.save(userActivity);
        } else log.info("authentication is not instance of UsernamePasswordAuthenticationToken");

        this.resolver.resolveException(request, response, null, accessDeniedException);
    }

}
