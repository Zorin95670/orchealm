package com.orchealm.orchealmapi.config;

import com.orchealm.orchealmapi.controller.filter.UserAuthenticationFilter;
import com.orchealm.orchealmapi.model.user.UserMapper;
import com.orchealm.orchealmapi.service.TeamService;
import com.orchealm.orchealmapi.service.UserService;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Gese API application.
 *
 * <p>This class defines the security filter chain based on the {@code auth.enabled}
 * property, allowing dynamic enabling/disabling of JWT authentication.
 *
 * <p>CSRF protection is disabled and the application is stateless (no HTTP sessions).
 */
@Configuration
public class SecurityConfig {
    /**
     * Service used to load user details and perform authentication-related operations.
     */
    private final UserService userService;
    private final UserMapper userMapper;
    private final TeamService teamService;
    private final TaskExecutor teamTaskExecutor;
    private final String externalIdClaim;
    private final String nameClaim;
    private final String emailClaim;
    private final String jwkSetUri;
    private final String issuerUri;

    @Autowired
    public SecurityConfig(final UserService userService,
                          final UserMapper userMapper,
                          final TeamService teamService,
                          final TaskExecutor teamTaskExecutor,
                          final @Value("${jwt.claims.externalId}") String externalIdClaim,
                          final @Value("${jwt.claims.name}") String nameClaim,
                          final @Value("${jwt.claims.email}") String emailClaim,
                          final @Value("${jwt.jwk-set-uri}") String jwkSetUri,
                          final @Value("${jwt.issuer-uri}") String issuerUri) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.teamService = teamService;
        this.teamTaskExecutor = teamTaskExecutor;
        this.externalIdClaim = externalIdClaim;
        this.nameClaim = nameClaim;
        this.emailClaim = emailClaim;
        this.jwkSetUri = jwkSetUri;
        this.issuerUri = issuerUri;
    }

    /**
     * Public endpoints (no authentication, no custom filter).
     *
     * @param http the {@link HttpSecurity} to configure
     * @return the configured {@link SecurityFilterChain}
     */
    @Bean
    @Order(1)
    public SecurityFilterChain publicEndpoints(final HttpSecurity http) {
        http
            .securityMatcher(
                "/health",
                "/actuator/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
            )
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    /**
     * Secured endpoints (JWT + custom filter).
     *
     * @param http the {@link HttpSecurity} to configure
     * @return the configured {@link SecurityFilterChain}
     */
    @Bean
    @Order(2)
    public SecurityFilterChain securedEndpoints(final HttpSecurity http) {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(
                org.springframework.security.config.http.SessionCreationPolicy.STATELESS
            ))
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .addFilterAfter(
                new UserAuthenticationFilter(
                    userService,
                    userMapper,
                    teamService,
                    teamTaskExecutor,
                    externalIdClaim,
                    nameClaim,
                    emailClaim
                ),
                BearerTokenAuthenticationFilter.class
            );

        return http.build();
    }

    /**
     * Configures the OpenAPI documentation with a security scheme for bearer token authentication.
     *
     * <p>This configuration adds a security scheme named {@code bearerAuth} using the HTTP Bearer authentication
     * method with JWT tokens. It also registers a global security requirement so that all endpoints in the Swagger UI
     * require this authentication unless explicitly overridden.</p>
     *
     * <p>This allows the Swagger UI to display an "Authorize" button where users can input their JWT
     * Bearer token, which will be sent as an {@code Authorization} header in subsequent requests.</p>
     *
     * @return the configured {@link OpenAPI} instance for Swagger documentation
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(new Components().addSecuritySchemes("bearerAuth",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
            ))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    @Bean
    public JwtDecoder jwtDecoder() {

        NimbusJwtDecoder decoder = NimbusJwtDecoder
            .withJwkSetUri(jwkSetUri)
            .build();

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        decoder.setJwtValidator(withIssuer);

        return decoder;
    }
}

