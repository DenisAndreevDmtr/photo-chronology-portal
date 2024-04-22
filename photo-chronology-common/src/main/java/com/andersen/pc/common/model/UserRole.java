package com.andersen.pc.common.model;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum UserRole implements GrantedAuthority {
    ADMIN("ADMIN"),
    USER("USER"),
    TEST("TEST");

    private final String name;

    @Override
    public String getAuthority() {
        return name;
    }
}
