package no.fdk.dataservicecatalog.service.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecification;
import no.fdk.dataservicecatalog.exceptions.ParseException;
import no.fdk.dataservicecatalog.model.OpenAPIInfo;
import no.fdk.dataservicecatalog.model.OpenAPIMeta;

public interface Parser {

    enum ApiType {
        OPENAPI("openapi"),
        SWAGGER("swagger");

        public final String label;

        ApiType(String label) {
            this.label = label;
        }

    }

    boolean canParse(String spec);

    ApiSpecification parse(String spec) throws ParseException;

    static boolean isValidSwaggerOrOpenApiV3(String spec, ApiType apiType, String minimalVersion) {
        OpenAPIMeta specMeta = readMandatoryMetaProperties(spec);
        if (specMeta != null) {
            String version;
            if (apiType == ApiType.OPENAPI) {
                version = specMeta.getOpenapi();
            } else {
                version = specMeta.getSwagger();
            }
            if (version == null || !(version.length() > 2 && version.startsWith(minimalVersion))) {
                return false;
            }
            OpenAPIInfo info = specMeta.getInfo();
            if (info == null) {
                return false;
            }

            String title = info.getTitle();
            if (title == null || title.isEmpty()) {
                return false;
            }

            String documentVersion = info.getVersion();
            return documentVersion != null && !documentVersion.isEmpty();
        } else {
            return false;
        }
    }

    private static OpenAPIMeta readMandatoryMetaProperties(String spec) {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(spec, OpenAPIMeta.class);
        } catch (Exception e) {
            return null;
        }
    }
}
