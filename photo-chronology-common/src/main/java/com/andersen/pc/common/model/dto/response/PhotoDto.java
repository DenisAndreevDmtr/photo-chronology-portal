package com.andersen.pc.common.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Token data")
public record PhotoDto(
        @Schema(description = "photo id",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,
        @Schema(description = "Link of the photo",
                example = "key",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String awsKey,
        @Schema(description = "Title of the photo",
                example = "title",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
        @Schema(description = "Created by")
        String createdBy,
        @Schema(description = "Created at")
        LocalDateTime createdAt,
        @Schema(description = "Modified by")
        String modifiedBy,
        @Schema(description = "Modified at")
        LocalDateTime modifiedAt) implements RequestResponse {
}
