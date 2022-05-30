package no.fdk.dataservicecatalog.service.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import no.fdk.dataservicecatalog.exceptions.ParseException;
import no.fdk.dataservicecatalog.model.ApiType;
import no.fdk.dataservicecatalog.model.OpenAPIInfo;
import no.fdk.dataservicecatalog.model.OpenAPIMeta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtils {

    public static boolean isValidSwaggerOrOpenApiV3(OpenAPIMeta specMeta, ApiType apiType, String majorVersion) {
        if (specMeta == null) {
            return false;
        } else {
            String version;
            if (apiType == ApiType.OPENAPI) {
                version = specMeta.getOpenapi();
            } else {
                version = specMeta.getSwagger();
            }

            if (version == null || !isValidSemVerWithCorrectMajor(version, majorVersion)) {
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
        }
    }

    private static boolean isValidSemVerWithCorrectMajor(String semver, String major) {
        Pattern versionPattern = Pattern.compile("^" + major + "\\.[0-9]+(\\.[0-9]+)?");
        Matcher matcher = versionPattern.matcher(semver);

        return matcher.matches();
    }
    public static OpenAPIMeta readMandatoryMetaProperties(String spec) throws ParseException {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(spec, OpenAPIMeta.class);
        } catch (Exception e) {
            throw new ParseException("Unable to read mandatory properties: " + e.getMessage());
        }
    }
}
