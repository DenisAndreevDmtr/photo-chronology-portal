package com.andersen.pc.common.controller;

import com.andersen.pc.common.model.dto.request.TokenRequest;
import com.andersen.pc.common.model.dto.response.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "Security", description = "Security management")
public interface SecurityController {

    @PostMapping("/token")
    @Operation(summary = "Generate access token for user")
    TokenDto getToken(TokenRequest tokenRequest);
}
