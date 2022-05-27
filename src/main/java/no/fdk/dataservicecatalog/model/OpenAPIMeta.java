package no.fdk.dataservicecatalog.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpenAPIMeta {
    private String openapi;
    private String swagger;
    private OpenAPIInfo info;
}
