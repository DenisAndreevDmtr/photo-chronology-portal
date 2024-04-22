package com.andersen.pc.common.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import static com.andersen.pc.common.constant.Constant.Errors.INVALID_EMAIL_FORMAT;
import static com.andersen.pc.common.constant.Constant.Errors.INVALID_NAME_SIZE;

@Schema(description = "Token data")
public record UserUpdateRequest(

        @Size(min = 2, max = 50, message = INVALID_NAME_SIZE)
        @Schema(description = "User update name",
                example = "new name",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String nameUpdate,
        @Email(message = INVALID_EMAIL_FORMAT)
        @Schema(description = "User update e-mail",
                example = "my-update-email@mail.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String emailUpdate,
        @Schema(description = "User id",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Positive
        Long userId) {
}
