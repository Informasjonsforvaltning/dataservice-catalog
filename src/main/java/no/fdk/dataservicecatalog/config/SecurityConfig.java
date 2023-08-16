package no.fdk.dataservicecatalog.config;

import no.fdk.dataservicecatalog.security.PermissionManager;
import no.fdk.dataservicecatalog.security.RDFMatcher;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

@Configuration
public class SecurityConfig {

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
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
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http, OAuth2ResourceServerProperties oAuth2ResourceServerProperties) throws Exception {
        http
            .csrf(spec -> spec.disable())
            .cors(spec -> spec.configurationSource(corsConfigurationSource()))
            .authorizeExchange(spec -> spec
                    .pathMatchers(HttpMethod.OPTIONS).permitAll()
                    .pathMatchers(HttpMethod.GET, "/ping").permitAll()
                    .pathMatchers(HttpMethod.GET, "/ready").permitAll()
                    .matchers(new RDFMatcher()).permitAll()
                    .pathMatchers(HttpMethod.DELETE)
                        .access((PermissionManager.of("organization", "write")))
                    .pathMatchers(HttpMethod.PATCH)
                        .access((PermissionManager.of("organization", "write")))
                    .pathMatchers(HttpMethod.POST)
                        .access((PermissionManager.of("organization", "write")))
                    .anyExchange()
                        .access((PermissionManager.of("organization", "read"))))
            .oauth2ResourceServer(spec -> spec.jwt(jwtSpec -> jwtSpec.jwtDecoder(jwtDecoder(oAuth2ResourceServerProperties))));

        return http.build();
    }

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
