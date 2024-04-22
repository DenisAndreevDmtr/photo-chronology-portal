package com.andersen.pc.portal.factory;

import com.andersen.pc.common.model.entity.User;
import com.andersen.pc.common.model.entity.UserRole;

public class UserRoleFactory {
    public static UserRole getUserRoleEntityObject(User user) {
        UserRole userRole = new UserRole();
        userRole.setId(user.getId());
        userRole.setUser(user);
        userRole.setRole(RoleFactory.getRoleEntityObject());
        return userRole;
    }
}
