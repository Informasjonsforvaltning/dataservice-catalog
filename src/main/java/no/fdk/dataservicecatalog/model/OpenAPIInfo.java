package no.fdk.dataservicecatalog.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class OpenAPIInfo {
    @Builder.Default
    private String title = "";
    @Builder.Default
    private String version = "";
}
