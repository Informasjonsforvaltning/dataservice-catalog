package no.fdk.dataservicecatalog.utils.jwk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;

import java.util.UUID;

public class JwkStore {
    private static final RSAKey jwk = createJwk();

    private static RSAKey createJwk() {
        try {
            return new RSAKeyGenerator(2048)
                    .algorithm(JWSAlgorithm.RS256)
                    .keyUse(KeyUse.SIGNATURE)
                    .keyID(UUID.randomUUID().toString())
                    .generate();
        } catch (JOSEException ex) {
            return null;
        }
    }

    public static String get() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JwkToken token = objectMapper.readValue(jwk.toJSONString(), JwkToken.class);
            return token.toString();
        } catch (JsonProcessingException ex) {
            return "";
        }
    }

    public static JWSHeader jwtHeader() {
        return new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(jwk.getKeyID()).build();
    }

    public static RSASSASigner signer() {
        try {
            return new RSASSASigner(jwk);
        } catch (JOSEException ex) {
            return null;
        }
    }
}
