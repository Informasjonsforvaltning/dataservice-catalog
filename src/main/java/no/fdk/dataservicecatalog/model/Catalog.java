package no.fdk.dataservicecatalog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Catalog {
    private String id;
    private Integer dataServiceCount;
}
