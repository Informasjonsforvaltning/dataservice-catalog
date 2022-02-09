package no.fdk.dataservicecatalog.utils.jwk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwkToken {
    private String kid;
    private String kty;
    private String use;
    private String n;
    private String e;

    @Override
    public String toString() {
        return "{" +
                "\"keys\": [" +
                "{" +
                "\"kid\": \"" + kid + "\"," +
                "\"kty\": \"" + kty + "\"," +
                "\"alg\": \"RS256\"," +
                "\"use\": \"" + use + "\"," +
                "\"n\": \"" + n + "\"," +
                "\"e\": \"" + e + "\"" +
                "}" +
                "]" +
                "}";
    }

}
