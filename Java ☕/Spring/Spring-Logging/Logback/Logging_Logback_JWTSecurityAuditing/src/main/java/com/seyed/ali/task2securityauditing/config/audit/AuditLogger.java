package com.seyed.ali.task2securityauditing.config.audit;

import com.seyed.ali.task2securityauditing.model.entity.MyUserPrincipal;
import com.seyed.ali.task2securityauditing.model.entity.UserActivity;
import com.seyed.ali.task2securityauditing.model.enums.ActivityType;
import com.seyed.ali.task2securityauditing.repository.UserActivityRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static com.seyed.ali.task2securityauditing.model.enums.ActivityType.FAILED_AUTHENTICATION;
import static com.seyed.ali.task2securityauditing.model.enums.ActivityType.SUCCESS_AUTHENTICATION;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogger {

    private final UserActivityRepository userActivityRepository;
    private final HttpServletRequest request;

    @EventListener
    public void auditOnSuccessAuthentication(AuthenticationSuccessEvent authenticationSuccessEvent) {
        Authentication authentication = authenticationSuccessEvent.getAuthentication();

        // This is for when the user authenticates himself, not whenever he hits all the secured endpoints
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            MyUserPrincipal userPrincipal = (MyUserPrincipal) authentication.getPrincipal();
            String remoteAddr = this.request.getRemoteAddr();
            String sessionId = this.request.getSession().getId();

            log.info("Login Success: Principal '{}' - '{}'", userPrincipal.getUsername(), SUCCESS_AUTHENTICATION);
            log.info("Remote IP address: {}", remoteAddr);
            log.info("Session Id: {}", sessionId);

            long userId = userPrincipal.getHogwartsUser().getId().longValue();
            saveUserActivity(
                    UserActivity.builder().createdBy(userId),
                    userPrincipal.getUsername(),
                    remoteAddr,
                    SUCCESS_AUTHENTICATION
            );
        }
    }

    /*@EventListener
    public void auditOnFailedAuthentication(AuditApplicationEvent auditApplicationEvent) {
        AuditEvent auditEvent = auditApplicationEvent.getAuditEvent();
        log.info("Login Failed: Principal '{}' - '{}'", auditEvent.getPrincipal(), auditEvent.getType());

        WebAuthenticationDetails details = (WebAuthenticationDetails) auditEvent.getData().get("details");
        log.info("Remote IP address: {}", details.getRemoteAddress());
        log.info("Session Id: {}", details.getSessionId());
        log.info("Request URL: {}", auditEvent.getData().get("requestUrl"));

        MyUserPrincipal principal = (MyUserPrincipal) auditEvent.getPrincipal();
        UserActivity userActivity = UserActivity.builder()
                .createdBy()
                .activityType(ActivityType.FAILED_AUTHENTICATION)
                .build();
        this.userActivityRepository.save(userActivity);
    }*/

    @EventListener
    public void auditOnFailedAuthenticationBadCredentials(AuthenticationFailureBadCredentialsEvent failureEvent) {
        String username = failureEvent.getAuthentication().getName();
        log.info("Login Failure: Principal '{}' - '{}'", username, FAILED_AUTHENTICATION);

        String remoteAddr = this.request.getRemoteAddr();
        log.info("Remote IP address: {}", remoteAddr);
        log.info("Attempted at: {}", Instant.now());

        saveUserActivity(
                UserActivity.builder().createdBy(null),
                username,
                remoteAddr,
                FAILED_AUTHENTICATION
        );
    }

    private void saveUserActivity(
            UserActivity.UserActivityBuilder userActivityBuilder,
            String username,
            String remoteAddr,
            ActivityType failedAuthentication
    ) {
        UserActivity userActivity = userActivityBuilder
                .createdByName(username)
                .ipAddress(remoteAddr)
                .activityType(failedAuthentication)
                .build();

        this.userActivityRepository.save(userActivity);
    }

}
