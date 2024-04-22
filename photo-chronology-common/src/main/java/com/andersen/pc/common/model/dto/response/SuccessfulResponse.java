package com.andersen.pc.common.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Successful response information")
public record SuccessfulResponse(
        @Schema(description = "Mark request accomplish successfully")
        Boolean success) {
}
