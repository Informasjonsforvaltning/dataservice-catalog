package no.fdk.dataservicecatalog.model;

public enum ApiType {
    OPENAPI("openapi"),
    SWAGGER("swagger");

    public final String label;

    ApiType(String label) {
        this.label = label;
    }
}
