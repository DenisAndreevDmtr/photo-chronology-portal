package com.andersen.pc.portal.security.factory;

import com.andersen.pc.common.exception.DbObjectConflictException;
import com.andersen.pc.common.model.UserRole;
import com.andersen.pc.common.model.entity.User;
import com.andersen.pc.portal.repository.UserRepository;
import com.andersen.pc.portal.security.jwt.JwtAuthentication;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.andersen.pc.common.constant.Constant.Errors.USER_NOT_FOUND;
import static com.andersen.pc.common.constant.Constant.Service.Token.TOKEN_CLAIM_EMAIL;
import static com.andersen.pc.common.constant.Constant.Service.Token.TOKEN_CLAIM_ROLES;
import static com.andersen.pc.common.constant.Constant.Service.Token.TOKEN_CLAIM_USERNAME;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFactory {

    private final UserRepository userRepository;

    public JwtAuthentication create(Claims claims) {
        String email = claims.get(TOKEN_CLAIM_EMAIL, String.class);
        return JwtAuthentication.builder()
                .email(email)
                .userName(claims.get(TOKEN_CLAIM_USERNAME, String.class))
                .roles(getRoles(claims))
                .userId(getUserId(email))
                .authenticated(Boolean.TRUE)
                .build();
    }

    private Set<UserRole> getRoles(Claims claims) {
        List<String> roles = claims.get(TOKEN_CLAIM_ROLES, List.class);
        return roles.stream()
                .map(UserRole::valueOf)
                .collect(Collectors.toSet());
    }

    private Long getUserId(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new DbObjectConflictException(USER_NOT_FOUND));
        return user.getId();
    }
}
