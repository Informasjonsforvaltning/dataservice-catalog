package no.fdk.dataservicecatalog.security;

import lombok.RequiredArgsConstructor;
import no.fdk.dataservicecatalog.config.SecurityProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.FixedAuthoritiesExtractor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final SecurityProperties securityProperties;
    private AuthoritiesExtractor authoritiesExtractor = new FixedAuthoritiesExtractor();

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return NimbusReactiveJwtDecoder.withJwkSetUri(securityProperties.getJwksUri())
                .build().decode(authentication.getCredentials().toString())
                .flatMap(jwt -> {
                    var authorities = authoritiesExtractor.extractAuthorities(jwt.getClaims());
                    var auth = new OpenIDAuthenticationToken(authentication.getPrincipal(), authorities, "", Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    return Mono.just(auth);
                });
    }
}
