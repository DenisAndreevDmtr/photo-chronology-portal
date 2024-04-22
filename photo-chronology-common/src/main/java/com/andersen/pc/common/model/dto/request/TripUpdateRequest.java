package com.andersen.pc.common.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

import static com.andersen.pc.common.constant.Constant.Errors.INVALID_TITLE_SIZE;

@Schema(description = "Trip data for update")
public record TripUpdateRequest(
        @Schema(description = "Title update",
                example = "my best trip")
        @Size(max = 100, message = INVALID_TITLE_SIZE)
        String titleUpdate,
        @Schema(description = "Date of the trip",
                example = "2024-02-02")
        @PastOrPresent
        LocalDate dateUpdate,
        @Schema(description = "trip id",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Positive
        Long tripId) {
}
