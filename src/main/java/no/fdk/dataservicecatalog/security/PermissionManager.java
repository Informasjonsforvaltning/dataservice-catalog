package no.fdk.dataservicecatalog.security;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.openid.OpenIDAuthenticationStatus;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
public class PermissionManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final Pattern catalogIdPattern = Pattern.compile("/catalogs/(.+?)/");
    private String resourceId;
    private String permission;

    public static PermissionManager of(String resourceId, String permission) {
        return new PermissionManager(resourceId, permission);
    }

    private Collection<ResourceRole> getResourceRoles(Collection<GrantedAuthority> authorities) {
        return authorities.stream()
                .map(Object::toString)
                .map(ResourceRoleFactory::deserialize)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        return mono.cast(OpenIDAuthenticationToken.class).map(auth -> {
            if (auth.isAuthenticated() && auth.getStatus().equals(OpenIDAuthenticationStatus.SUCCESS)) {
                var path = authorizationContext.getExchange().getRequest().getPath().pathWithinApplication().value();
                if (path.startsWith("/catalogs/")) {
                    Matcher matcher = catalogIdPattern.matcher(path);
                    if (matcher.find()) {
                        var catalogId = matcher.group(1);
                        return new AuthorizationDecision(getResourceRoles(auth.getAuthorities()).stream().anyMatch(rr -> rr.matchPermission(resourceId, catalogId, permission)));
                    }
                }
            }
            return new AuthorizationDecision(false);
        });
    }
}
