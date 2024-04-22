package com.andersen.pc.portal.api.controller;

import com.andersen.pc.common.model.dto.request.UserCreationRequest;
import com.andersen.pc.common.model.dto.request.UserPasswordResetRequest;
import com.andersen.pc.common.model.dto.request.UserSearchRequest;
import com.andersen.pc.common.model.dto.request.UserUpdateRequest;
import com.andersen.pc.common.model.dto.response.RequestResult;
import com.andersen.pc.common.model.dto.response.SuccessfulResponse;
import com.andersen.pc.common.model.dto.response.UserDto;
import com.andersen.pc.portal.security.jwt.JwtAuthentication;
import com.andersen.pc.portal.service.AuthenticationService;
import com.andersen.pc.portal.service.UserPasswordService;
import com.andersen.pc.portal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements com.andersen.pc.common.controller.UserController {

    private final UserService userService;
    private final UserPasswordService userPasswordService;
    private final AuthenticationService authenticationService;

    @Override
    public UserDto createUser(UserCreationRequest userCreationRequest) {
        return userService.createUser(userCreationRequest);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(T(com.andersen.pc.common.model.UserRole).ADMIN," +
            " T(com.andersen.pc.common.model.UserRole).USER)")
    public UserDto getUser(Long id) {
        return userService.findUserById(id);
    }

    @Override
    @PreAuthorize("hasAuthority(T(com.andersen.pc.common.model.UserRole).ADMIN) || " +
            "hasAuthority(T(com.andersen.pc.common.model.UserRole).USER) && " +
            "@authenticationService.authenticationOwnerUser(#id)")
    public ResponseEntity<SuccessfulResponse> deleteUser(Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok(new SuccessfulResponse(Boolean.TRUE));
    }

    @Override
    @PreAuthorize("hasAuthority(T(com.andersen.pc.common.model.UserRole).ADMIN) || " +
            "hasAuthority(T(com.andersen.pc.common.model.UserRole).USER)")
    public UserDto updateUser(UserUpdateRequest userUpdateRequest) {
        JwtAuthentication authentication = authenticationService.getAuthentication();
        return userService.updateUser(userUpdateRequest, authentication);
    }

    @Override
    @PreAuthorize("hasAuthority(T(com.andersen.pc.common.model.UserRole).ADMIN) || " +
            "hasAuthority(T(com.andersen.pc.common.model.UserRole).USER) && " +
            "@authenticationService.authenticationOwnerUser(#id)")
    public ResponseEntity<SuccessfulResponse> chaneUserPassword(
            UserPasswordResetRequest userPasswordResetRequest,
            Long id) {
        userPasswordService.changeUserPassword(id, userPasswordResetRequest);
        return ResponseEntity.ok(new SuccessfulResponse(Boolean.TRUE));
    }

    @Override
    @PreAuthorize("hasAnyAuthority(T(com.andersen.pc.common.model.UserRole).ADMIN)")
    public UserDto deactivateUser(Long id) {
        return userService.deactivateUserById(id);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(T(com.andersen.pc.common.model.UserRole).ADMIN," +
            " T(com.andersen.pc.common.model.UserRole).USER)")
    public RequestResult getUsersBySearchParameters(
            UserSearchRequest userSearchRequest,
            Pageable pageable) {
        Pair<Long, List<UserDto>> userDtoPairs = userService.getUsersBySearchParameters(userSearchRequest, pageable);
        return new RequestResult(userDtoPairs.getKey(), userDtoPairs.getValue());
    }
}
