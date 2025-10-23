package no.fdk.dataservicecatalog.utils.jwk;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.val;

import java.util.Date;
import java.util.List;

public class JwtToken {
    private static final long exp = new Date().getTime() + 120 * 1000;
    private static List<String> aud = List.of("dataservice-catalog");

    private static String authFromAccess(Access access) {
        var authorities = "";
        switch(access) {
            case ORG_READ:
                authorities = "organization:123456789:read,organization:246813579:read,organization:111111111:read,organization:111222333:read";
                break;
            case ORG_WRITE:
                authorities = "organization:123456789:admin,organization:246813579:write,organization:111111111:write,organization:111222333:admin";
                break;
            case ROOT:
                authorities = "system:root:admin";
                break;
        }
        return authorities;
    }

    public static String buildToken(Access access){
        try {
            val claims = new JWTClaimsSet.Builder()
                    .audience(aud)
                    .expirationTime(new Date(exp))
                    .claim("iss", "https://keycloak.staging.fellesdatakatalog.digdir.no/realms/fdk")
                    .claim("user_name", "1924782563")
                    .claim("name", "TEST USER")
                    .claim("given_name", "TEST")
                    .claim("family_name", "USER")
                    .claim("authorities", authFromAccess(access))
                    .build();

            val signed = new SignedJWT(JwkStore.jwtHeader(), claims);
            signed.sign(JwkStore.signer());

            return signed.serialize();
        } catch (JOSEException ex) {
            return "";
        }
    }
}
