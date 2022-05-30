package no.fdk.dataservicecatalog.service.parser;

import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecification;
import no.fdk.dataservicecatalog.exceptions.ParseException;
import no.fdk.dataservicecatalog.model.OpenAPIMeta;

public class UniversalParser {
    static private final OpenApiV3Parser openApiParser = new OpenApiV3Parser();
    static private final SwaggerParser swaggerParser = new SwaggerParser();
    public boolean canParse(String spec) throws ParseException {
        OpenAPIMeta specMeta = ParserUtils.readMandatoryMetaProperties(spec);
        return openApiParser.canParse(specMeta) || swaggerParser.canParse(specMeta);
    }

    public ApiSpecification parse(String spec) throws ParseException {
        ApiSpecification parsed;
        OpenAPIMeta specMeta = ParserUtils.readMandatoryMetaProperties(spec);

        if (openApiParser.canParse(specMeta)) {
            parsed = openApiParser.parse(spec);
        } else if (swaggerParser.canParse(specMeta)) {
            parsed = swaggerParser.parse(spec);
        } else {
            throw new ParseException("Available parsers not able to parse source specification");
        }

        return parsed;
    }
}
