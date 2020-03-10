package no.fdk.dataservicecatalog.dto.shared.apispecification.paths;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PathItem {

    private String summary;
    private String description;
    private Operation get;
    private Operation put;
    private Operation post;
    private Operation delete;
    private Operation options;
    private Operation head;
    private Operation patch;
    private Operation trace;

}
