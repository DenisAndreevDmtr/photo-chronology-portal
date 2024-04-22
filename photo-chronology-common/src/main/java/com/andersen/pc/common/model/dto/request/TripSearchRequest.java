package com.andersen.pc.common.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

import static com.andersen.pc.common.constant.Constant.Errors.SEARCH_PARAMETER_SHOULD_BE_GREATER_2_SYMBOLS;

@Schema(description = "Trip data for search")
public record TripSearchRequest(
        @Schema(description = "Trip title",
                example = "best trip")
        @Size(min = 2, message = SEARCH_PARAMETER_SHOULD_BE_GREATER_2_SYMBOLS)
        String title,
        @Schema(description = "Date from",
                example = "2024-01-01")
        @PastOrPresent
        LocalDate dateFrom,
        @Schema(description = "Date to",
                example = "2024-02-02")
        @PastOrPresent
        LocalDate dateTo) {
}
