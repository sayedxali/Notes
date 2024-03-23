# Keycloak in Monolith Application

<!-- TOC -->
* [Keycloak in Monolith Application](#keycloak-in-monolith-application)

* [Configure Keycloak](#configure-keycloak)

* [Configure Spring App](#configure-spring-app)
  * [Using Opaque Token](#using-opaque-token)
  * [Authorization (JWT)](#authorization-jwt)
    * [Custom Converter](#custom-converter)
    * [Default Converter](#default-converter)
  
* [Swagger](#swagger)

* [Authorization within Keycloak](#authorization-within-keycloak)
  * [Keycloak](#keycloak)
  * [SpringBoot](#springboot)
  
* [SSO](#sso)
  * [Keycloak](#keycloak-1)
  
  * [SpringBoot](#springboot-1)

  * [Integrating With Google-Cloud](#integrating-with-google-cloud)
  
    <!-- TOC -->





[TOC]





For Starting, we'll first need to configure a client and users in keycloak, then the spring boot application. 

# Configure Keycloak

1. Run the keycloak server using Docker (or the jar): 

   ```powershell
   docker run -p 9494:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:24.0.1 start-dev
   ```

2. Login to keycloak admin panel and create a <kbd>realm</kbd>.

3. Create a <kbd>client</kbd>.

   ![image-20240314123120299](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314123120299.png)

   ![image-20240314123242180](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314123242180.png)

   ![image-20240314123406128](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314123406128.png)

   <blockquote alt = 'green'>Now Save:<br>Client_ID, Client_Secret, Issuer, Token_Endpoint, Introspect_Token, Logout_Token, Refresh_Token </blockquote>

   >"issuer": <span alt = 'purple'>For spring boot app configuration</span> "http://localhost:9494/realms/dive-into-development"
   >
   >
   >
   >"token_endpoint": <span alt = 'purple'>For generating Token</span> "http://localhost:9494/realms/dive-into-development/protocol/openid-connect/token".
   >
   >
   >
   >"introspection_endpoint": <span alt = 'purple'>For opaque Token</span> "http://localhost:9494/realms/dive-into-development/protocol/openid-connect/token/introspect".

4. Create <kbd>roles</kbd>.

   ![image-20240314124017800](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314124017800.png)

5. Now Spring Boot Application.

# Configure Spring App

1. Dependencies:

   - oauth2-resource-server
   - security
   - web

2. Security Config:

   We can secure our application in 2 ways: using JWT or Opaque token.

   ## Using JWT Token

   ![image-20240314125358600](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314125358600.png)

   ![image-20240314130302513](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314130302513.png)
   

   ```java
   @Configuration
   @EnableWebSecurity
   @EnableMethodSecurity
   public class SecurityConfig {
   
       @Bean
       public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
           http.csrf(AbstractHttpConfigurer::disable)
                   .authorizeHttpRequests(customizer -> customizer
                           .requestMatchers(GET, "/restaurant/public/list").permitAll()
                           .requestMatchers(GET, "/restaurant/public/**").permitAll()
                           .anyRequest().authenticated()
                   )
                   .oauth2ResourceServer(configurer ->
                                   configurer.jwt(Customizer.withDefaults())
   //                                configurer.opaqueToken(Customizer.withDefaults())
                   )
                   .sessionManagement(
                           customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                   );
   
           return http.build();
       }
   }
   ```

   And the configuration:

   ```yaml
   spring:
     security:
       oauth2:
         resourceserver:
           jwt:
             issuer-uri: http://localhost:9494/realms/dive-into-development
           opaquetoken:
             client-id: dive-into-development
             client-secret: Y6m8n71cdgbw1SV5I7HNRqdZdKAVPlaF
             introspection-uri: http://localhost:9494/realms/dive-into-development/protocol/openid-connect/token/introspect
   ```

   <blockquote alt = 'purple'>Note 💡:<br>While using JWT Token, when we hit logout, and we use the same access_token for accessing the resources (our endpoints), we will still be able to access the endpoints and the logout would be for nothing.<br><br>This is because the token is validated inside the oauth2-resource-server and not in the keycloak so the server (our app) doesn't knows that the token has logged-out, the keycloak knows.</blockquote>

   > Note 💡: 
   >
   > We'll use <span alt = 'purple'>token_endpoint[^1]</span> to generate `access_token` to be able to access the resources (endpoints).
   
   [^1]: We'll need <kbd>client-id</kbd>, <kbd>client-secret</kbd>, <kbd>grant-type: password</kbd>, <kbd>username</kbd>, <kbd>password</kbd>.
   
   
   
   ## Using Opaque Token
   
   ![image-20240314130428092](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314130428092.png)
   
   ![image-20240314131000693](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314131000693.png)
   
   
   
   ```java
   @Configuration
   @EnableWebSecurity
   @EnableMethodSecurity
   public class SecurityConfig {
   
       @Bean
       public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
           http.csrf(AbstractHttpConfigurer::disable)
                   .authorizeHttpRequests(customizer -> customizer
                           .requestMatchers(GET, "/restaurant/public/list").permitAll()
                           .requestMatchers(GET, "/restaurant/public/**").permitAll()
                           .anyRequest().authenticated()
                   )
                   .oauth2ResourceServer(configurer ->
   //                                configurer.jwt(Customizer.withDefaults())
                                   configurer.opaqueToken(Customizer.withDefaults())
                   )
                   .sessionManagement(
                           customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                   );
   
           return http.build();
       }
   }
   ```
   
   And the configuration:
   
   ```yaml
   spring:
     security:
       oauth2:
         resourceserver:
           opaquetoken:
             client-id: dive-into-development
             client-secret: Y6m8n71cdgbw1SV5I7HNRqdZdKAVPlaF
             introspection-uri: http://localhost:9494/realms/dive-into-development/protocol/openid-connect/token/introspect
   ```
   
   ## Authorization (JWT)
   
   ![image-20240314132349300](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314132349300.png)
   
   What happens here, in the method level authorization; the incoming JWT token from the keycloak is converted into a format which fits proper to the spring boots oauth2 format. 
   It retrieve the roles from the token, and create a `JwtAuthenticationToken`:
   
   ![image-20240314132709787](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314132709787.png)
   
   When the access_token is received, what it does is, it checks for property "scope" or "scp" in the jwt token. It will pick those and append "SCOPE_" to those. These are added to `JwtAuthenticationToken` class as roles. This class is used to authorize the endpoints.
   
   > The problem is, in the scope property, we may not have the roles that we defined (realm_roles). In that essence, we need a converter to tell spring that we'll use another property and make those as roles.
   
   <blockquote alt = 'purple'>Note 💡:<br>If we don't want to make our own converter and use oauth2's default converter, we can do that too. We just need to define a bean of <kbd>JwtAuthenticationConverter</kbd>.</blockquote>
   
   ### Custom Converter
   
   Security configuration
   
   ```java
   @Configuration
   @EnableWebSecurity
   @EnableMethodSecurity
   @RequiredArgsConstructor
   public class SecurityConfig {
   
       private final KeyCloakJwtAuthConverter keyCloakJwtAuthConverter;
   
       @Bean
       public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
           http.csrf(AbstractHttpConfigurer::disable)
                   .authorizeHttpRequests(customizer -> customizer
                           .requestMatchers("/token").permitAll()
                           .requestMatchers(POST, "/profile").hasRole("SCOPE_profile")
                           .requestMatchers(GET, "/restaurant/public/list").permitAll()
                           .requestMatchers(GET, "/restaurant/public/**").permitAll()
                           .anyRequest().authenticated()
                   )
                   .oauth2ResourceServer(configurer ->
                                   configurer.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(keyCloakJwtAuthConverter))
                   )
                   .sessionManagement(
                           customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                   );
   
           return http.build();
       }
   
       // to get rid of "ROLE_"
       @Bean
       public DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
           DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler = new DefaultMethodSecurityExpressionHandler();
           defaultMethodSecurityExpressionHandler.setDefaultRolePrefix("");
           return defaultMethodSecurityExpressionHandler;
       }
   }
   ```
   
   Custom Converter(KeyCloakJwtAuthConverter)
   
   ```java
   @Component
   @RequiredArgsConstructor
   public class KeyCloakJwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
   
       private final ObjectMapper objectMapper;
   
       @Override
       public AbstractAuthenticationToken convert(Jwt jwt) {
           Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
           return new JwtAuthenticationToken(jwt, authorities);
       }
   
       //////////////////////////////////////////////////////////////////////////////////
       /* VIDEO 8.2 DiveIntoDevelopment */
       //////////////////////////////////////////////////////////////////////////////////
   
       private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
           Map<String, Object> realmAccess = jwt.getClaim("realm_access");
   
           if (realmAccess == null)
               return new ArrayList<>();
   
           List<String> keycloakRoles = this.objectMapper.convertValue(realmAccess.get("roles"), List.class);
           List<GrantedAuthority> roles = new ArrayList<>();
   
           for (String keycloakRole : keycloakRoles)
   //                roles.add(new SimpleGrantedAuthority(STR."ROLE_\{keycloakRole}"));
               roles.add(new SimpleGrantedAuthority(keycloakRole));
   
           return roles;
       }
   
   }
   ```
   
   ### Default Converter
   
   Security configuration
   
   ```java
   @Configuration
   @EnableWebSecurity
   @EnableMethodSecurity
   public class SecurityConfig {
   
       @Bean
       public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
           http.csrf(AbstractHttpConfigurer::disable)
                   .authorizeHttpRequests(customizer -> customizer
                           .requestMatchers(GET, "/restaurant/public/list").permitAll()
                           .anyRequest().authenticated()
                   )
                   .oauth2ResourceServer(configurer ->
                                   configurer.jwt(Customizer.withDefaults())
                   )
                   .sessionManagement(
                           customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                   );
   
           return http.build();
       }
   
       // to get rid of "ROLE_"
       @Bean
       public DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
           DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler = new DefaultMethodSecurityExpressionHandler();
           defaultMethodSecurityExpressionHandler.setDefaultRolePrefix("");
           return defaultMethodSecurityExpressionHandler;
       }
   
       //////////////////////////////////////////////////////////////////////////////////
       						/* VIDEO 8.3 DiveIntoDevelopment */
       //////////////////////////////////////////////////////////////////////////////////
   
       // to use default jwt authentication converter and the roles to be in the jwt level;
       @Bean
       public JwtAuthenticationConverter jwtAuthenticationConverter() {
           JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
           authoritiesConverter.setAuthorityPrefix(""); // default "SCOPE_"
           authoritiesConverter.setAuthoritiesClaimName("roles"); // default "scope" or "scp"
   
           JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
           converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
           return converter;
       }
   
   }
   ```
   



# Swagger

1. Dependency:

   ```yaml
   <dependency>
       <groupId>org.springdoc</groupId>
       <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
   	<version>2.2.0</version>
   </dependency>
   ```

2. Create a configuration class

   ```java
   @Configuration
   @SecurityScheme(
           name = "Keycloak",
           openIdConnectUrl = "http://localhost:9494/realms/dive-into-development/.well-known/openid-configuration",
           scheme = "bearer",
           type = SecuritySchemeType.OPENIDCONNECT,
           in = SecuritySchemeIn.HEADER
   )
   public class SwaggerConfig {
   }
   ```

3. Controllers

   ```java
   @RestController
   @RequestMapping("/order")
   @SecurityRequirement(name = "Keycloak")
   public class OrderController {
       ...
   }
   ```

4. SecurityConfig

   ```java
   @Configuration
   @EnableWebSecurity
   @EnableMethodSecurity
   public class SecurityConfig {
   
       @Bean
       public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
           http.csrf(AbstractHttpConfigurer::disable)
                   .authorizeHttpRequests(customizer -> customizer
                           .requestMatchers(GET, "/restaurant/public/list").permitAll()
                           .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() ///////////////////////////////////////// NEW LINE
                           .anyRequest().authenticated()
                   )
                   .oauth2ResourceServer(configurer ->
                                   configurer.jwt(Customizer.withDefaults())
                   )
                   .sessionManagement(
                           customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                   );
   
           return http.build();
       }
   
       // to get rid of "ROLE_"
       @Bean
       public DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
           DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler = new DefaultMethodSecurityExpressionHandler();
           defaultMethodSecurityExpressionHandler.setDefaultRolePrefix("");
           return defaultMethodSecurityExpressionHandler;
       }
   
       //////////////////////////////////////////////////////////////////////////////////
       						/* VIDEO 8.3 DiveIntoDevelopment */
       //////////////////////////////////////////////////////////////////////////////////
   
       // to use default jwt authentication converter and the roles to be in the jwt level;
       @Bean
       public JwtAuthenticationConverter jwtAuthenticationConverter() {
           JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
           authoritiesConverter.setAuthorityPrefix(""); // default "SCOPE_"
           authoritiesConverter.setAuthoritiesClaimName("roles"); // default "scope" or "scp"
   
           JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
           converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
           return converter;
       }
   
   }
   ```

5. application.yaml file

   ```yaml
   springdoc:
     swagger-ui:
       oauth:
         client-id: dive-into-development
         client-secret: Y6m8n71cdgbw1SV5I7HNRqdZdKAVPlaF
   ```

   

# Authorization within Keycloak

## Keycloak

1. Go into your client

   ![image-20240314140946857](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314140946857.png)

   ![image-20240314141041750](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314141041750.png)

   > We'll be working with resources, policies and permissions. First delete the default one and we'll create our own.

2. Create Resources

   ![image-20240314141245130](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314141245130.png)

3. Create Policy

   ![image-20240314141346821](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314141346821.png)

   Add the default realms role

   ![image-20240314141424715](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314141424715.png)

4. Create Permission

   ![image-20240314141632209](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314141632209.png)

## SpringBoot

1. Dependency

   ```yaml
   <dependency>
       <groupId>org.keycloak</groupId>
       <artifactId>keycloak-spring-boot-starter</artifactId>
       <version>22.0.3</version>
   </dependency>
   <dependency>
       <groupId>org.keycloak</groupId>
       artifactId>keycloak-servlet-filter-adapter</artifactId>
   	<version>22.0.3</version>
   </dependency>
   ```

2. SecurityConfig

   ```java
   @Configuration
   @EnableWebSecurity
   // @EnableMethodSecurity //////////////////////////// NEW -> removed this annotation
   @RequiredArgsConstructor
   public class SecurityConfig {
   
       private final KeyCloakJwtAuthConverter keyCloakJwtAuthConverter;
   
       @Bean
       public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
           http.csrf(AbstractHttpConfigurer::disable)
               	// removed all authorization and oauth2 configs
                   .addFilterAfter(createPolicyEnforcerFilter(), BearerTokenAuthenticationFilter.class)
                   .sessionManagement(
                           customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                   );
   
           return http.build();
       }
   
       private ServletPolicyEnforcerFilter createPolicyEnforcerFilter() {
           return new ServletPolicyEnforcerFilter(new ConfigurationResolver() {
               @Override
               public PolicyEnforcerConfig resolve(HttpRequest httpRequest) {
                   try {
   
                       return JsonSerialization.readValue(
                               getClass().getResourceAsStream("/policy-enforcer.json"),
                               PolicyEnforcerConfig.class
                       );
   
                   } catch (Exception e) {
                       throw new RuntimeException(e);
                   }
               }
           });
       }
   
   }
   ```

   policy-enforcer.json

   ```json
   {
     "realm": "dive-into-development",
     "auth-server-url": "http://localhost:8080",
     "resource": "dive-into-development",
     "credentials": {
       "secret": "Y6m8n71cdgbw1SV5I7HNRqdZdKAVPlaF"
     },
     "paths": [
       {
         "path": "/restaurant/public/list",
         "enforcement-mode": "DISABLED"
       },
       {
         "path": "/restaurant/public/menu/*",
         "enforcement-mode": "DISABLED"
       },
       {
         "path": "/swagger-ui/*",
         "enforcement-mode": "DISABLED"
       },
       {
         "path": "/v3/api-doc/*",
         "enforcement-mode": "DISABLED"
       }
     ]
   }
   ```



...





# SSO

## Keycloak

1. Edit the AuthenticationFlow

   ![image-20240314150220467](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314150220467.png)

2. Create a new client with the same configuration of the previous one; but the `valid redirect_uri` must be different.

## SpringBoot

Create an application with the same configs as the previous one.

application.yaml file

```yaml
spring:
  application:
    name: TestSSO-DiveIntoDevelopment
server:
  port: 8090
---
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:9494/realms/dive-into-development
---
springdoc:
  swagger-ui:
    oauth:
      client-id: test-sso-dive-into-development
      client-secret: j6imyRES32xIB0xq4Nd2Fi7gt5JVA5Au
```

SecurityConfig

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(customizer -> customizer
                        .requestMatchers("/token").permitAll()
                        .requestMatchers(POST, "/profile").hasRole("SCOPE_profile")
                        .requestMatchers(GET, "/restaurant/public/list").permitAll()
                        .requestMatchers(GET, "/restaurant/public/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // NEW LINE
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(configurer ->
                        configurer.jwt(Customizer.withDefaults())
                )
                .sessionManagement(
                        customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    // to get rid of "ROLE_"
    @Bean
    public DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler = new DefaultMethodSecurityExpressionHandler();
        defaultMethodSecurityExpressionHandler.setDefaultRolePrefix("");
        return defaultMethodSecurityExpressionHandler;
    }

    //////////////////////////////////////////////////////////////////////////////////
    /* VIDEO 8.3 */
    //////////////////////////////////////////////////////////////////////////////////

    // to use default jwt authentication converter and the roles to be in the jwt level;
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix(""); // default "SCOPE_"
        authoritiesConverter.setAuthoritiesClaimName("roles"); // default "scope" or "scp"

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }

}
```

Controller

```java
@RestController
@RequestMapping("/test")
@SecurityRequirement(name = "Keycloak")
public class TestController {

    @GetMapping
    public String testingSSO() {
        return "Testing SSO!";
    }

}
```

SwaggerConfig

```java
@Configuration
@SecurityScheme(
        name = "Keycloak",
        openIdConnectUrl = "http://localhost:9494/realms/dive-into-development/.well-known/openid-configuration",
        scheme = "bearer",
        type = SecuritySchemeType.OPENIDCONNECT,
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
}
```

## Integrating With Google-Cloud

1. Head to https://console.cloud.google.com/

2. Login:

   ![image-20240314151854934](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314151854934.png)

   ![image-20240314152027216](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314152027216.png)
   
   ![image-20240314152120554](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314152120554.png)
   
   ![image-20240314152256083](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314152256083.png)
   
   ![image-20240314152818524](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314152818524.png)
   
   ![image-20240314152903686](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314152903686.png)
   
   ![image-20240314153138655](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314153138655.png)
   
   ![image-20240314153406159](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314153406159.png)
   
   ![image-20240314153523582](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314153523582.png)
   
   ![image-20240314154351754](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314154351754.png)
   
   ![image-20240314154609677](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240314154609677.png)
   
   Done!
   
   
