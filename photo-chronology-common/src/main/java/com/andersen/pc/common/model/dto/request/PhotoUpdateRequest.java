package com.andersen.pc.common.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import static com.andersen.pc.common.constant.Constant.Errors.INVALID_TITLE_SIZE;

@Schema(description = "Photo data for update")
public record PhotoUpdateRequest(
        @Schema(description = "Photo title", example = "title update")
        @Size(min = 2, max = 100, message = INVALID_TITLE_SIZE)
        String titleUpdate,
        @Schema(description = "Photo id",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Positive
        Long photoId) {
}
