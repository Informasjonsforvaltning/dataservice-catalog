package no.fdk.dataservicecatalog.dto.shared.apispecification.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Info {

    private String title;
    private String description;
    private String termsOfService;
    private Contact contact;
    private License license;
    private String version;

}

