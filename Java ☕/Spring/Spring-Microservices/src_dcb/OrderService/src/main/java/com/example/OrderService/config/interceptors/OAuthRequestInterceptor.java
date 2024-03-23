package com.example.OrderService.config.interceptors;

import com.example.OrderService.OrderServiceApplication;
import com.example.OrderService.config.security.OktaOAuth2WebSecurity;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

/**
 * Step 2: interceptor for feign client.
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
@RequiredArgsConstructor
public class OAuthRequestInterceptor implements RequestInterceptor {

    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager; // created a bean

    @Override
    public void apply(RequestTemplate template) {
        String bearerToken = oAuth2AuthorizedClientManager.authorize(
                OAuth2AuthorizeRequest.withClientRegistrationId("internal-client") // this is the client registration name
                        .principal("internal") // this is the scope
                        .build()
        ).getAccessToken().getTokenValue();

        template.header("Authorization", "Bearer " + bearerToken);
    }

}
