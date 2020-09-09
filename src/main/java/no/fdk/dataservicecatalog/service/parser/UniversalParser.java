package no.fdk.dataservicecatalog.service.parser;

import no.fdk.dataservicecatalog.dto.shared.apispecification.ApiSpecification;
import no.fdk.dataservicecatalog.exceptions.ParseException;

import java.util.Arrays;

public class UniversalParser implements Parser {
    static private final Parser[] parsers = {
        new OpenApiV3JsonParser(),
        new SwaggerJsonParser()
    };

    public boolean canParse(String spec) {
        return Arrays.stream(parsers).anyMatch(parser -> parser.canParse(spec));
    }

    public ApiSpecification parse(String spec) throws ParseException {

        Parser selectedParser = Arrays.stream(parsers)
            .filter(parser -> parser.canParse(spec))
            .findFirst()
            .orElseThrow(() -> new ParseException("No suitable parsers found"));

        return selectedParser.parse(spec);
    }
}
