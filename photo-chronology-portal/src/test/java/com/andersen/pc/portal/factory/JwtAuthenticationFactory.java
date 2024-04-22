package com.andersen.pc.portal.factory;

import com.andersen.pc.common.model.UserRole;
import com.andersen.pc.common.model.entity.User;
import com.andersen.pc.portal.security.jwt.JwtAuthentication;

import java.util.Set;
import java.util.stream.Collectors;

public class JwtAuthenticationFactory {

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "John Lane";
    private static final String E_MAIL = "test@mail.com";

    public static JwtAuthentication getObject() {
        User user = UserFactory.getEntityObject();
        Set<UserRole> roles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole()
                        .getRoleName())
                .collect(Collectors.toSet());

        return JwtAuthentication.builder()
                .userId(user.getId())
                .userName(user.getName())
                .email(user.getEmail())
                .roles(roles)
                .authenticated(Boolean.TRUE)
                .build();
    }
}
