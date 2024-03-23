package com.seyed.ali.task2securityauditing.config.security;

import com.seyed.ali.task2securityauditing.config.security.jwt.CustomBasicAuthenticationEntryPoint;
import com.seyed.ali.task2securityauditing.config.security.jwt.CustomBearerTokenAccessDeniedHandler;
import com.seyed.ali.task2securityauditing.config.security.jwt.CustomBearerTokenAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private @Value("${api.endpoint.base-url}") String BASE_URL;

    private final CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;
    private final CustomBearerTokenAuthenticationEntryPoint customBearerAuthenticationEntryPoint;
    private final CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler;

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector handlerMappingIntrospector) {
        return new MvcRequestMatcher.Builder(handlerMappingIntrospector);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvcBuilder) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(mvcBuilder.pattern(GET, STR."\{this.BASE_URL}/artifacts/**")).permitAll()
                        .requestMatchers(mvcBuilder.pattern(GET, STR."\{this.BASE_URL}/users/**")).hasAuthority("ROLE_admin") // Protect this endpoint
                        .requestMatchers(mvcBuilder.pattern(POST, STR."\{this.BASE_URL}/users")).hasAuthority("ROLE_admin") // Protect this endpoint
                        .requestMatchers(mvcBuilder.pattern(PUT, STR."\{this.BASE_URL}/users/**")).hasAuthority("ROLE_admin") // Protect this endpoint
                        .requestMatchers(mvcBuilder.pattern(DELETE, STR."\{this.BASE_URL}/users/**")).hasAuthority("ROLE_admin") // Protect this endpoint
                        .requestMatchers("/log/public/**").permitAll()
                        .requestMatchers(antMatcher("/h2-console/**")).permitAll()
                        // Disallow everything else.
                        .anyRequest().authenticated() // Always a good idea to put this as last.
                ).headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) // This is for H2 browser console access.
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(this.customBasicAuthenticationEntryPoint))
                .oauth2ResourceServer(
                        oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults())
                                .authenticationEntryPoint(this.customBearerAuthenticationEntryPoint)
                                .accessDeniedHandler(this.customBearerTokenAccessDeniedHandler)
                )
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(STATELESS));

        return http.build();
    }

    @Bean
    public PasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder(12); // 2^12 hashing iteration
    }

}
