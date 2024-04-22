package com.andersen.pc.common.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collection;
@Schema(description = "Result of the request")
public record RequestResult(
        @Schema(description = "Total count of records for the request not considering Pageable")
        Long totalCount,
        @Schema(description = "Result of the request")
        Collection<? extends RequestResponse> requestResult) {
}
