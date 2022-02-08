package no.fdk.dataservicecatalog.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class SecurityUtils {

    public static String getAuthorities(SecurityContext securityContext) {
        try {
            Jwt jwt = (Jwt) securityContext.getAuthentication().getPrincipal();
            return (String) jwt.getClaims().get("authorities");
        } catch (Exception ex) {
            log.error("unable to extract jwt authorities", ex);
            return "";
        }
    }

    public static boolean isSysAdmin(String authorities) {
        return authorities.contains(SystemRootAdminRole.systemType + ":" + SystemRootAdminRole.rootId + ":" + SystemRootAdminRole.resourceRole);
    }

    public static List<String> authCatalogs(String authorities) {
        Pattern catalogIdPattern = Pattern.compile("[0-9]{9}");
        return Arrays.stream(authorities.split(":"))
                .filter(authPart -> catalogIdPattern.matcher(authPart).matches())
                .collect(Collectors.toList());
    }

}
