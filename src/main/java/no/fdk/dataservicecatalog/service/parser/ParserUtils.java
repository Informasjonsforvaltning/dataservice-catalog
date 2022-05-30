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

    public static boolean isValidSwaggerOrOpenApiV3(OpenAPIMeta specMeta, ApiType apiType, String majorVersion) throws ParseException {
        if (specMeta == null) {
            throw new ParseException("Source specification missing mandatory fields openapi/swagger & info");
        } else {
            String openapi = specMeta.getOpenapi();
            String swagger = specMeta.getSwagger();
            if (openapi == null && swagger == null) {
                throw new ParseException("Source specification missing mandatory field openapi/swagger");
            }

            OpenAPIInfo info = specMeta.getInfo();
            if (info == null) {
                throw new ParseException("Source specification missing mandatory field info");
            }

            String title = info.getTitle();
            if (title == null || title.isEmpty()) {
                throw new ParseException("Source specification missing mandatory field info.title");
            }

            String documentVersion = info.getVersion();
            if(documentVersion == null || documentVersion.isEmpty()) {
                throw new ParseException("Source specification missing mandatory field info.version");
            }

            if (apiType == ApiType.OPENAPI) {
                return openapi != null && isValidSemVerWithCorrectMajor(openapi, majorVersion);
            } else {
                return swagger != null && isValidSemVerWithCorrectMajor(swagger, majorVersion);
            }
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
