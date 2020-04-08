package no.fdk.dataservicecatalog.dto.acat;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Access {

//    @ApiModelProperty("Indication if the api is from an authoritative source (a National Component)")
    private Boolean isAuthoritativeSource;

//    @ApiModelProperty("Indication if the api has open access")
    private Boolean isOpenAccess;

//    @ApiModelProperty("Indication if the api has open licence")
    private Boolean isOpenLicense;

//    @ApiModelProperty("Indication if the api is free")
    private Boolean isFree;

}
