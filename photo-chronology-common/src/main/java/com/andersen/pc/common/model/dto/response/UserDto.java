package com.andersen.pc.common.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Token data")
public record UserDto(
        @Schema(description = "User id", requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,
        @Schema(description = "User name", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "User e-mail", example = "my-email@mail.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @Schema(
                description = "User status",
                allowableValues = {"ACTIVE", "DISABLED"},
                requiredMode = Schema.RequiredMode.REQUIRED)
        String status,
        @Schema(
                description = "User roles",
                allowableValues = {"ADMIN", "USER"},
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "[\"USER\"]")
        Set<String> roles,
        @Schema(description = "Created by")
        String createdBy,
        @Schema(description = "Created at")
        LocalDateTime createdAt,
        @Schema(description = "Modified by")
        String modifiedBy,
        @Schema(description = "Modified at")
        LocalDateTime modifiedAt) implements RequestResponse {
}
