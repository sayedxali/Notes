package code.with.bingyang.wei.hogwartsartifactsonline.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
public class SecurityConfiguration {

    private @Value("${api.endpoint.base-url}") String BASE_URL;

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;
    private final CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;
    private final CustomBearerTokenAuthenticationEntryPoint customBearerAuthenticationEntryPoint;
    private final CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler;


    public SecurityConfiguration(CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint, CustomBearerTokenAuthenticationEntryPoint customBearerAuthenticationEntryPoint, CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler) throws NoSuchAlgorithmException {
        this.customBasicAuthenticationEntryPoint = customBasicAuthenticationEntryPoint;
        this.customBearerAuthenticationEntryPoint = customBearerAuthenticationEntryPoint;
        this.customBearerTokenAccessDeniedHandler = customBearerTokenAccessDeniedHandler;

        // Generate a private/public key pair.
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // The generated key will have a size of  2048 bits.

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.publicKey = ((RSAPublicKey) keyPair.getPublic());
        this.privateKey = ((RSAPrivateKey) keyPair.getPrivate());
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector handlerMappingIntrospector) {
        return new MvcRequestMatcher.Builder(handlerMappingIntrospector);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvcBuilder) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(mvcBuilder.pattern(GET, this.BASE_URL + "/artifacts/**")).permitAll()
                        .requestMatchers(mvcBuilder.pattern(GET, this.BASE_URL + "/users/**")).hasAuthority("ROLE_admin") // Protect this endpoint
                        .requestMatchers(mvcBuilder.pattern(POST, this.BASE_URL + "/users")).hasAuthority("ROLE_admin") // Protect this endpoint
                        .requestMatchers(mvcBuilder.pattern(PUT, this.BASE_URL + "/users/**")).hasAuthority("ROLE_admin") // Protect this endpoint
                        .requestMatchers(mvcBuilder.pattern(DELETE, this.BASE_URL + "/users/**")).hasAuthority("ROLE_admin") // Protect this endpoint
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

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build();
        JWKSource<SecurityContext> jwtSet = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwtSet);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("authorities"); // Since we are defining our own custom claim |-> `authorities`
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(""); // Since default authority prefix is `SCOPE_` |-> if we don't write this line, it'll add this before the authority claim |=> `SCOPE_ROLE_admin`

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

}
