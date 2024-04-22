package com.andersen.pc.common.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import static com.andersen.pc.common.constant.Constant.Errors.EMAIL_MUST_BE_SET;
import static com.andersen.pc.common.constant.Constant.Errors.INVALID_EMAIL_FORMAT;
import static com.andersen.pc.common.constant.Constant.Errors.PASSWORD_MUST_BE_SET;

public record TokenRequest(
        @Schema(description = "User e-mail",
                example = "admin@email.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = EMAIL_MUST_BE_SET)
        @Email(message = INVALID_EMAIL_FORMAT)
        String email,
        @Schema(description = "User password",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "admin")
        @NotEmpty(message = PASSWORD_MUST_BE_SET)
        char[] password) {
}
