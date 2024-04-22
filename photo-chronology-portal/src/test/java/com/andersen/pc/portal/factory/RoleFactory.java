package com.andersen.pc.portal.factory;

import com.andersen.pc.common.model.UserRole;
import com.andersen.pc.common.model.entity.Role;

public class RoleFactory {

    private static final Long ROLE_ID = 2L;

    public static Role getRoleEntityObject() {
        return Role.builder()
                .id(ROLE_ID)
                .roleName(UserRole.USER)
                .build();
    }
}
