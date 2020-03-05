package no.fdk.dataservicecatalog.dto.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Publisher {

    private String uri;

    private String id;

    private String name;

    private String orgPath;

    private Map<String, String> prefLabel;

}
