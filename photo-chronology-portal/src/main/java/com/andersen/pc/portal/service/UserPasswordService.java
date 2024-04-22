package com.andersen.pc.portal.service;

import com.andersen.pc.common.exception.DbObjectConflictException;
import com.andersen.pc.common.exception.DomainObjectValidationException;
import com.andersen.pc.common.model.dto.request.UserPasswordResetRequest;
import com.andersen.pc.common.model.entity.User;
import com.andersen.pc.common.model.entity.UserPassword;
import com.andersen.pc.portal.repository.UserPasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.andersen.pc.common.constant.Constant.Errors.INCORRECT_OLD_PASSWORD;
import static com.andersen.pc.common.constant.Constant.Errors.PASSWORD_NOT_FOUND;
import static com.andersen.pc.common.constant.Constant.Errors.USER_PASSWORD_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserPasswordService {

    private final UserPasswordRepository userPasswordRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UserPassword findPasswordByUser(User user) {
        return userPasswordRepository.findByUser(user)
                .orElseThrow(() -> new DbObjectConflictException(PASSWORD_NOT_FOUND));
    }

    @Transactional
    public void changeUserPassword(
            Long userId,
            UserPasswordResetRequest userPasswordResetRequest) {
        UserPassword userPassword = userPasswordRepository.findByUserId(userId)
                .orElseThrow(() -> new DbObjectConflictException(USER_PASSWORD_NOT_FOUND));
        if (!isPasswordMatches(userPasswordResetRequest.oldPassword(), userPassword.getPasswordHash())) {
            throw new DomainObjectValidationException(INCORRECT_OLD_PASSWORD);
        }
        updatePassword(userPasswordResetRequest.newPassword(), userPassword);
        tokenService.deleteTokensByUserId(userId);
    }

    public void createPasswordForUser(User user, char[] password) {
        UserPassword userPassword = new UserPassword();
        userPassword.setUser(user);
        String newEncodedPassword = getEncodedPassword(password);
        userPassword.setPasswordHash(newEncodedPassword);
        userPasswordRepository.save(userPassword);

    }

    private boolean isPasswordMatches(char[] rawPassword, String passwordHash) {
        StringBuilder passwordBuilder = new StringBuilder().append(rawPassword);
        return passwordEncoder.matches(passwordBuilder, passwordHash);
    }

    private void updatePassword(char[] password, UserPassword userPassword) {
        String newEncodedPassword = getEncodedPassword(password);
        userPassword.setPasswordHash(newEncodedPassword);
        userPasswordRepository.save(userPassword);
    }

    private String getEncodedPassword(char[] password) {
        StringBuilder builder = new StringBuilder().append(password);
        return passwordEncoder.encode(builder);
    }
}
