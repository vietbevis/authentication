package com.vietbevis.authentication.common;

public enum RoleBase {
    ADMIN,
    USER;

    public static boolean isBaseRole(String roleName) {
        return roleName.equals(ADMIN.name()) || roleName.equals(USER.name());
    }

}
