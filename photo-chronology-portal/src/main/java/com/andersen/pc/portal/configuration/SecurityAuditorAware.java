package com.andersen.pc.portal.configuration;

import com.andersen.pc.portal.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;

import java.util.Objects;
import java.util.Optional;

import static com.andersen.pc.common.constant.Constant.Service.PC_PORTAL;

public class SecurityAuditorAware implements AuditorAware<String> {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = authenticationService.getAuthentication();
        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            return Optional.of(PC_PORTAL);
        }
        return Optional.ofNullable(authentication.getName());
    }
}
