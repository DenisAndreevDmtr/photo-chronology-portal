package com.andersen.pc.common.model.dto.request;

import com.andersen.pc.common.model.annotation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import static com.andersen.pc.common.constant.Constant.Errors.NEW_PASSWORD_MUST_BE_SET;
import static com.andersen.pc.common.constant.Constant.Errors.OLD_PASSWORD_MUST_BE_SET;

@Schema(description = "Data for reset password")
public record UserPasswordResetRequest(
        @Schema(
                description = "Previous user password",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "MyStrongPass@123987")
        @NotEmpty(message = OLD_PASSWORD_MUST_BE_SET)
        char[] oldPassword,
        @Schema(
                description = "New user password",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "MyNewStrongPass@123987")
        @NotEmpty(message = NEW_PASSWORD_MUST_BE_SET)
        @ValidPassword
        char[] newPassword) {
}