package no.fdk.dataservicecatalog.dto.acat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Status {

    private String statusText;

    private LocalDateTime expirationDate;

    private String comment;

    private String supersededByUrl;
}
