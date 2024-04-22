package com.andersen.pc.portal.api.controller;

import com.andersen.pc.common.controller.SecurityController;
import com.andersen.pc.common.model.dto.request.TokenRequest;
import com.andersen.pc.common.model.dto.response.TokenDto;
import com.andersen.pc.portal.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SecurityControllerImpl implements SecurityController {
    private final AuthenticationService authenticationService;

    @Override
    public TokenDto getToken(TokenRequest tokenRequest) {
        return authenticationService.generateAccessToken(tokenRequest);
    }
}
