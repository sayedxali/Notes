package com.example.OrderService.config.security;

import com.example.OrderService.OrderServiceApplication;
import com.example.OrderService.config.interceptors.OAuthRequestInterceptor;
import com.example.OrderService.config.interceptors.RestTemplateInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Step 1: security configuration.
 * <p>Related Classes:
 * <ui>
 * <li>{@link OktaOAuth2WebSecurity} : security configuration.</li>
 * <li>{@link OAuthRequestInterceptor} : interceptor for feign client.</li>
 * <li>{@link OrderServiceApplication} : creating a bean.</li>
 * <li>{@link RestTemplateInterceptor} : interceptor for RestTemplate.</li>
 * <li>{@code application.yaml} : okta configuration part</li>
 * </ui>
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OktaOAuth2WebSecurity {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().authenticated()
                ).oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

        return http.build();
    }

}
