package com.andersen.pc.common.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Token data")
public record TripDto(
        @Schema(description = "Trip id", requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,
        @Schema(description = "Date of the trip",
                example = "2024-01-01",
                requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate date,
        @Schema(description = "Title of the trip",
                example = "My best trip",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
        @Schema(description = "User id",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long userId,
        @Schema(description = "Created by", requiredMode = Schema.RequiredMode.REQUIRED)
        Set<PhotoDto> photos,
        @Schema(description = "Created by")
        String createdBy,
        @Schema(description = "Created at")
        LocalDateTime createdAt,
        @Schema(description = "Modified by")
        String modifiedBy,
        @Schema(description = "Modified at")
        LocalDateTime modifiedAt) implements RequestResponse {
}
