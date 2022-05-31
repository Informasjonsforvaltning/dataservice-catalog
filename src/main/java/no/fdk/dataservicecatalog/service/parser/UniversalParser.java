package no.fdk.dataservicecatalog.service.parser;

import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecification;
import no.fdk.dataservicecatalog.exceptions.ParseException;
import no.fdk.dataservicecatalog.model.OpenAPIMeta;

public class UniversalParser {
    static private final OpenApiV3Parser openApiParser = new OpenApiV3Parser();
    static private final SwaggerParser swaggerParser = new SwaggerParser();
    public boolean isParseable(String spec) throws ParseException {
        OpenAPIMeta specMeta = ParserUtils.readMandatoryMetaProperties(spec);
        return openApiParser.isParseable(specMeta) || swaggerParser.isParseable(specMeta);
    }

    public ApiSpecification parse(String spec) throws ParseException {
        ApiSpecification parsed;
        OpenAPIMeta specMeta = ParserUtils.readMandatoryMetaProperties(spec);

        if (openApiParser.isParseable(specMeta)) {
            parsed = openApiParser.parse(spec);
        } else if (swaggerParser.isParseable(specMeta)) {
            parsed = swaggerParser.parse(spec);
        } else {
            throw new ParseException("Available parsers not able to parse source specification");
        }

        return parsed;
    }
}
