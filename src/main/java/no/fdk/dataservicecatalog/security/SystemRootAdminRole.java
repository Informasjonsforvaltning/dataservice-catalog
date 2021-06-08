package no.fdk.dataservicecatalog.security;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public final class SystemRootAdminRole implements ResourceRole {
    static final String systemType = "system";
    static final String rootId = "root";
    static final String resourceRole = "admin";

    @Override
    public String getResourceType() {
        return systemType;
    }
    @Override
    public String getResourceId() {
        return rootId;
    }
    @Override
    public Boolean matchPermission(String type, String id, String permission) {
        return type.equals(systemType) && id.equals(rootId) && resourceRole.equals(permission);
    }

}
