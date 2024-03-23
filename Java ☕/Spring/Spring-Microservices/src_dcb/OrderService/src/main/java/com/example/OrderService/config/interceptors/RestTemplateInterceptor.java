package com.example.OrderService.config.interceptors;

import com.example.OrderService.OrderServiceApplication;
import com.example.OrderService.config.security.OktaOAuth2WebSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import java.io.IOException;

/**
 * Step 4: interceptor for RestTemplate.
 * <p>Related Classes:
 * <ui>
 * <li>{@link OktaOAuth2WebSecurity} : security configuration.</li>
 * <li>{@link OAuthRequestInterceptor} : interceptor for feign client.</li>
 * <li>{@link OrderServiceApplication} : creating a bean.</li>
 * <li>{@link RestTemplateInterceptor} : interceptor for RestTemplate.</li>
 * <li>{@code application.yaml} : okta configuration part</li>
 * </ui>
 */
@RequiredArgsConstructor
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String bearerToken = oAuth2AuthorizedClientManager.authorize(
                OAuth2AuthorizeRequest.withClientRegistrationId("internal-client")
                        .principal("internal")
                        .build()
        ).getAccessToken().getTokenValue();

        request.getHeaders().add("Authorization", "Bearer " + bearerToken);
        return execution.execute(request, body);
    }

}
