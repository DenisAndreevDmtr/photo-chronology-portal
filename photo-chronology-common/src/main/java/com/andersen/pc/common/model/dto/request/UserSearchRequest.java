package com.andersen.pc.common.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import static com.andersen.pc.common.constant.Constant.Errors.SEARCH_PARAMETER_SHOULD_BE_GREATER_2_SYMBOLS;
import static com.andersen.pc.common.constant.Constant.Errors.SEARCH_PARAMETER_SHOULD_BE_SET;

@Schema(description = "User data for search")
public record UserSearchRequest(
        @Schema(description = "User name",
                example = "user")
        @NotEmpty(message = SEARCH_PARAMETER_SHOULD_BE_SET)
        @Size(min = 2, message = SEARCH_PARAMETER_SHOULD_BE_GREATER_2_SYMBOLS)
        String searchParameter) {
}
