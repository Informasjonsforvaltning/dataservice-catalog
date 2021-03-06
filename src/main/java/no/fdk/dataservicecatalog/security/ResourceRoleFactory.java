package no.fdk.dataservicecatalog.security;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ResourceRoleFactory {

    static ResourceRole deserialize(String roleToken) {
        try {
            String[] parts = roleToken.split(":");
            String resourceType = parts[0];
            String resourceId = parts[1];
            String resourceRole = parts[2];

            SystemRootAdminRole sysAdmin = new SystemRootAdminRole();

            if (OrganizationResourceRole.resourceType.equals(resourceType)) {
                return new OrganizationResourceRole(resourceId, OrganizationResourceRole.OrganizationRole.valueOf(resourceRole));
            } else if (sysAdmin.matchPermission(resourceType, resourceId, resourceRole)) {
                return sysAdmin;
            }
            throw new IllegalArgumentException("Unknown resourceType");
        } catch (Exception e) {
            log.warn("Error parsing ResourceRole token: {}", roleToken);
            return null;
        }
    }

}
