package no.fdk.dataservicecatalog.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class OpenAPIMeta {
    @Builder.Default
    private String openapi = "";
    @Builder.Default
    private String swagger = "";
    private OpenAPIInfo info;
}
