package com.example.OrderService;

import com.example.OrderService.config.interceptors.OAuthRequestInterceptor;
import com.example.OrderService.config.interceptors.RestTemplateInterceptor;
import com.example.OrderService.config.security.OktaOAuth2WebSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Step 3: creating the beans for {@link OAuth2AuthorizedClientManager} that is being injected into the interceptors.
 * <p>Related Classes:
 * <ui>
 * <li>{@link OktaOAuth2WebSecurity} : security configuration.</li>
 * <li>{@link OAuthRequestInterceptor} : interceptor for feign client.</li>
 * <li>{@link OrderServiceApplication} : creating a bean.</li>
 * <li>{@link RestTemplateInterceptor} : interceptor for RestTemplate.</li>
 * <li>{@code application.yaml} : okta configuration part</li>
 * </ui>
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@RequiredArgsConstructor
public class OrderServiceApplication {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.setInterceptors(
                List.of(new RestTemplateInterceptor(
                                clientManager(clientRegistrationRepository, oAuth2AuthorizedClientRepository)
                        )
                )
        );

        return restTemplate;
    }

    @Bean
    public OAuth2AuthorizedClientManager clientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository
    ) {
        OAuth2AuthorizedClientProvider oAuth2AuthorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        DefaultOAuth2AuthorizedClientManager defaultOAuth2AuthorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, oAuth2AuthorizedClientRepository);
        defaultOAuth2AuthorizedClientManager.setAuthorizedClientProvider(oAuth2AuthorizedClientProvider);

        return defaultOAuth2AuthorizedClientManager;
    }

}
