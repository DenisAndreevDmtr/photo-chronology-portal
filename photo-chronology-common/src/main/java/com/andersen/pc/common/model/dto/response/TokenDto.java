package com.andersen.pc.common.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token data")
public record TokenDto(
        @Schema(description = "User token",
                example = "token",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String token,
        @Schema(description = "Token type",
                defaultValue = "bearer",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String tokenType,
        @Schema(description = "Token expires in",
                example = "12345",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long expiresIn,
        @Schema(description = "User ID",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long userId) {
}
