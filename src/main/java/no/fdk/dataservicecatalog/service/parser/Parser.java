package no.fdk.dataservicecatalog.service.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecification;
import no.fdk.dataservicecatalog.exceptions.ParseException;

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
        try {
            JsonElement element = new JsonParser().parse(spec);
            String version = element.getAsJsonObject().get(apiType.label).getAsString();
            if (!(version.length() > 2 && version.startsWith(minimalVersion))) {
                return false;
            }

            JsonElement info = element.getAsJsonObject().get("info");
            if (info == null) {
                return false;
            }

            String title = info.getAsJsonObject().get("title").getAsString();
            if (title == null || title.isEmpty()) {
                return false;
            }
            String documentVersion = info.getAsJsonObject().get("version").getAsString();
            return documentVersion != null && !documentVersion.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
