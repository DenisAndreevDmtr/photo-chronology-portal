package com.andersen.pc.portal.service;

import com.andersen.pc.common.exception.AuthenticationException;
import com.andersen.pc.common.exception.DomainObjectValidationException;
import com.andersen.pc.common.model.dto.request.TokenRequest;
import com.andersen.pc.common.model.dto.response.TokenDto;
import com.andersen.pc.common.model.entity.Token;
import com.andersen.pc.common.model.entity.User;

import com.andersen.pc.common.model.entity.UserPassword;
import com.andersen.pc.portal.security.jwt.JwtAuthentication;
import com.andersen.pc.portal.security.factory.TokenFactory;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.andersen.pc.common.constant.Constant.Errors.EMAIL_MUST_BE_SET;
import static com.andersen.pc.common.constant.Constant.Errors.INVALID_CREDENTIALS;
import static com.andersen.pc.common.constant.Constant.Errors.PASSWORD_MUST_BE_SET;
import static com.andersen.pc.common.constant.Constant.Errors.USER_IS_NOT_ACTIVE;
import static com.andersen.pc.common.model.UserStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final UserPasswordService userPasswordService;
    private final PasswordEncoder passwordEncoder;
    private final TokenFactory tokenFactory;
    private final TokenService tokenService;

    @Transactional
    public TokenDto generateAccessToken(TokenRequest tokenRequest) {
        User dataUser = getUserByToken(tokenRequest);
        User user = checkCredentials(dataUser, tokenRequest.password());
        Token token = tokenFactory.createAccessToken(user);
        return tokenService.saveAndGetApi(token, user.getId());
    }

    public JwtAuthentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication instanceof JwtAuthentication)
                ? (JwtAuthentication) authentication
                : null;
    }

    public boolean authenticationOwnerUser(@NotNull Long userId) {
        JwtAuthentication authentication = getAuthentication();
        return (Objects.nonNull(authentication)) ? userId.equals(authentication.getUserId()) : Boolean.FALSE;
    }

    private User getUserByToken(TokenRequest tokenRequest) {
        checkUserEmail(tokenRequest.email());
        checkUserPassword(tokenRequest.password());
        return userService.getUserByEmail(tokenRequest.email());
    }

    private User checkCredentials(User dataUser, char[] password) {
        UserPassword userPassword = userPasswordService.findPasswordByUser(dataUser);
        if (isPasswordCorrect(password, userPassword.getPasswordHash())) {
            if (!ACTIVE.equals(dataUser.getStatus())) {
                throw new AuthenticationException(USER_IS_NOT_ACTIVE);
            }
        } else {
            throw new AuthenticationException(INVALID_CREDENTIALS);
        }
        return dataUser;
    }

    private void checkUserEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new DomainObjectValidationException(EMAIL_MUST_BE_SET);
        }
    }

    private void checkUserPassword(char[] password) {
        if (ArrayUtils.isEmpty(password)) {
            throw new DomainObjectValidationException(PASSWORD_MUST_BE_SET);
        }
    }

    private boolean isPasswordCorrect(char[] rawPassword, String passwordHash) {
        StringBuilder passwordBuilder = new StringBuilder().append(rawPassword);
        return passwordEncoder.matches(passwordBuilder, passwordHash);
    }
}
