package com.andersen.pc.common.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

import static com.andersen.pc.common.constant.Constant.Errors.DATE_MUST_BE_SET;
import static com.andersen.pc.common.constant.Constant.Errors.INVALID_TITLE_SIZE;
import static com.andersen.pc.common.constant.Constant.Errors.TITLE_MUST_BE_SET;

@Schema(description = "Trip data for creation")
public record TripCreationRequest(
        @Schema(description = "Title",
                example = "best trip",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = TITLE_MUST_BE_SET)
        @Size(max = 100, message = INVALID_TITLE_SIZE)
        String title,
        @Schema(description = "Date of the trip",
                example = "2024-01-01",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = DATE_MUST_BE_SET)
        @PastOrPresent
        LocalDate date) {
}
