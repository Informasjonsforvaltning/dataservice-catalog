package no.fdk.dataservicecatalog.security;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

import java.util.Arrays;
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

    private Collection<ResourceRole> getResourceRoles(String authorities) {
        return Arrays.stream(authorities.split(","))
                .map(ResourceRoleFactory::deserialize)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        return authentication.cast(JwtAuthenticationToken.class).map(auth -> {
            if (auth.isAuthenticated()) {
                var path = authorizationContext.getExchange().getRequest().getPath().pathWithinApplication().value();
                if (path.contentEquals("/catalogs")) {
                    return new AuthorizationDecision(
                            getResourceRoles((String) auth.getTokenAttributes().get("authorities"))
                                    .stream()
                                    .anyMatch(rr -> rr instanceof OrganizationResourceRole || rr instanceof SystemRootAdminRole));
                }
                if (path.startsWith("/catalogs/")) {
                    Matcher matcher = catalogIdPattern.matcher(path);
                    if (matcher.find()) {
                        var catalogId = matcher.group(1);
                        return new AuthorizationDecision(
                                getResourceRoles((String) auth.getTokenAttributes().get("authorities"))
                                        .stream()
                                        .anyMatch(rr -> rr.matchPermission(resourceId, catalogId, permission) || rr instanceof SystemRootAdminRole));
                    }
                }
            }
            return new AuthorizationDecision(false);
        });
    }
}
