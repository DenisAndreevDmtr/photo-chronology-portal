package com.andersen.pc.common.model.dto.request;

import com.andersen.pc.common.model.annotation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import static com.andersen.pc.common.constant.Constant.Errors.EMAIL_MUST_BE_SET;
import static com.andersen.pc.common.constant.Constant.Errors.INVALID_EMAIL_FORMAT;
import static com.andersen.pc.common.constant.Constant.Errors.INVALID_EMAIL_SIZE;
import static com.andersen.pc.common.constant.Constant.Errors.INVALID_NAME_SIZE;
import static com.andersen.pc.common.constant.Constant.Errors.NAME_MUST_BE_SET;
import static com.andersen.pc.common.constant.Constant.Errors.PASSWORD_VALIDATION_FAILED;

@Schema(description = "User data for creation")
public record UserCreationRequest(
        @Schema(description = "User name",
                example = "name",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = NAME_MUST_BE_SET)
        @Size(min = 2, max = 50, message = INVALID_NAME_SIZE)
        String name,
        @Schema(description = "User e-mail",
                example = "my-email@mail.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = EMAIL_MUST_BE_SET)
        @Email(message = INVALID_EMAIL_FORMAT)
        @Size(max = 256, message = INVALID_EMAIL_SIZE)
        String email,
        @Schema(
                description = "New user password",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "MyStrongPass@123987")
        @NotEmpty(message = PASSWORD_VALIDATION_FAILED)
        @ValidPassword
        char[] password) {
}
