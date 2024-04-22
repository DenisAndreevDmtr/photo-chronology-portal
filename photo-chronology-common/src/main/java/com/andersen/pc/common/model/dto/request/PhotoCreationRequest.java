package com.andersen.pc.common.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import static com.andersen.pc.common.constant.Constant.Errors.INVALID_TITLE_SIZE;
import static com.andersen.pc.common.constant.Constant.Errors.TITLE_MUST_BE_SET;

@Schema(description = "Photo data for creation")
public record PhotoCreationRequest(
        @Schema(description = "Photo title",
                example = "title",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = TITLE_MUST_BE_SET)
        @Size(min = 2, max = 100, message = INVALID_TITLE_SIZE)
        String title,
        @Schema(description = "Trip id",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Positive
        Long tripId) {
}
