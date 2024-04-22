package com.andersen.pc.portal.factory;

import com.andersen.pc.common.model.UserStatus;
import com.andersen.pc.common.model.entity.User;
import com.andersen.pc.common.model.entity.UserRole;
import org.h2.engine.UserBuilder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class UserFactory {

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "Name";
    private static final String EMAIL = "email@email.com";
    private static final String CREATED_BY = "PORTAL";

    public static User getEntityObject() {
        User user = User.builder()
                .id(USER_ID)
                .email(EMAIL)
                .name(USER_NAME)
                .status(UserStatus.ACTIVE)
                .userRoles(null)
                .createdBy(CREATED_BY)
                .createdAt(LocalDateTime.now())
                .build();
        UserRole userRole = UserRoleFactory.getUserRoleEntityObject(user);
        Set<UserRole> userRoles = Set.of(userRole);
        user.setUserRoles(userRoles);
        return user;
    }
}
