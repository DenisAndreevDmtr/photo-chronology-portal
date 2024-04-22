package com.andersen.pc.common.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response information")
public record ErrorResponse(
        @Schema(description = "Error code", requiredMode = Schema.RequiredMode.REQUIRED)
        int code,
        @Schema(description = "Textual error description", requiredMode = Schema.RequiredMode.REQUIRED)
        String description) {
}
