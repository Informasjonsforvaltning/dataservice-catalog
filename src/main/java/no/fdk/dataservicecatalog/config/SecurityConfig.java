package no.fdk.dataservicecatalog.config;

import no.fdk.dataservicecatalog.security.PermissionManager;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    CorsConfigurationSource corsConfiguration() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.applyPermitDefaultValues();
        corsConfig.addAllowedMethod(HttpMethod.POST);
        corsConfig.addAllowedMethod(HttpMethod.PATCH);
        corsConfig.addAllowedMethod(HttpMethod.DELETE);
        corsConfig.setAllowedOrigins(Collections.singletonList("*"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf().disable();

        http.cors()
            .and().authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers(HttpMethod.GET, "/ping").permitAll()
                .pathMatchers(HttpMethod.GET, "/ready").permitAll()
                .pathMatchers(HttpMethod.GET, "/catalogs").permitAll()
                .pathMatchers(HttpMethod.GET, "/catalogs/{catalogId}").permitAll()
                .pathMatchers(HttpMethod.GET, "/catalogs/{catalogId}/**")
                    .access((PermissionManager.of("organization", "read")))
                .pathMatchers(HttpMethod.DELETE)
                    .access((PermissionManager.of("organization", "write")))
                .pathMatchers(HttpMethod.PATCH)
                    .access((PermissionManager.of("organization", "write")))
                .pathMatchers(HttpMethod.POST)
                    .access((PermissionManager.of("organization", "write")))
                .anyExchange().authenticated()
            .and().oauth2ResourceServer().jwt();

        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder(OAuth2ResourceServerProperties properties) {
        NimbusReactiveJwtDecoder jwtDecoder = NimbusReactiveJwtDecoder.withJwkSetUri(properties.getJwt().getJwkSetUri()).build();

        OAuth2TokenValidator<Jwt> audienceValidator = new JwtClaimValidator<List<String>>(
                JwtClaimNames.AUD, aud -> aud.contains("dataservice-catalog"));
        DelegatingOAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(), new JwtIssuerValidator(properties.getJwt().getIssuerUri()), audienceValidator);

        jwtDecoder.setJwtValidator(validator);
        return jwtDecoder;
    }
}
