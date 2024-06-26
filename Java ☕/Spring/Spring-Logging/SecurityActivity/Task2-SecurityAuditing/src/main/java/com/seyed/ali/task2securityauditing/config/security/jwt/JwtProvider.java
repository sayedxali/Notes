package com.seyed.ali.task2securityauditing.config.security.jwt;

import com.seyed.ali.task2securityauditing.model.entity.MyUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtEncoder jwtEncoder;

   /* public String createToken(Authentication authentication) {
        Instant now = Instant.now();
        long expiresIn = 2; // 2 hours

        // Prepare a claim called authorities.
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));// MUST BE space-delimited |-> `ROLE_admin ROLE_user`

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(expiresIn, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("authorities", authorities)
                .build();
        
        return this.jwtEncoder
                .encode(JwtEncoderParameters.from(claimsSet))
                .getTokenValue();
    }*/

    public String createToken(Authentication authentication) {
        MyUserPrincipal userPrincipal = (MyUserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getHogwartsUser().getId().longValue();

        Instant now = Instant.now();
        long expiresIn = 2; // 2 hours

        // Prepare a claim called authorities.
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));// MUST BE space-delimited |-> `ROLE_admin ROLE_user`


        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(expiresIn, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("userId", userId) // add the user's ID as a claim
                .claim("authorities", authorities)
                .build();

        return this.jwtEncoder
                .encode(JwtEncoderParameters.from(claimsSet))
                .getTokenValue();
    }

}
