package no.fdk.dataservicecatalog.dto.shared.apispecification.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contact {

    private String name;
    private String url;
    private String email;

}
