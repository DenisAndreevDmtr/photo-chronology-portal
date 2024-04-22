package com.andersen.pc.common.controller;

import com.andersen.pc.common.model.dto.request.TripCreationRequest;
import com.andersen.pc.common.model.dto.request.TripSearchRequest;
import com.andersen.pc.common.model.dto.request.TripUpdateRequest;
import com.andersen.pc.common.model.dto.response.RequestResult;
import com.andersen.pc.common.model.dto.response.SuccessfulResponse;
import com.andersen.pc.common.model.dto.response.TripDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Trip", description = "Trip's management")
@SecurityRequirement(name = "bearerAuth")
public interface TripController {

    @PostMapping("trips/creation")
    @Operation(summary = "Create new trip", description = "Create new trip")
    TripDto createTrip(@RequestBody @Valid TripCreationRequest tripCreationRequest);


    @GetMapping("trips/{id}")
    @Operation(summary = "Get trip by id", description = "Get trip by id")
    TripDto getTripById(@PathVariable Long id);

    @GetMapping("users/{id}/trips")
    @Operation(summary = "Get user's trips by search and sort parameters",
            description = "Get user's trips by search and sort parameters",
            parameters = {
                    @Parameter(in = ParameterIn.QUERY,
                            description = """
                                    Sorting criteria in the format: property,(asc|desc). Default sorting is id,ask.
                                    Multiple sort criteria are supported.<br />
                                    <ul>For sorting
                                        <li>by date: date;</li>
                                        <li>by title: title;</li>
                                    </ul>""",
                            name = "sort",
                            array = @ArraySchema(schema = @Schema(type = "string")))
            })
    RequestResult searchUserTrips(
            @Valid TripSearchRequest tripSearchRequest,
            @PathVariable Long id,
            @SortDefault(sort = "id") Pageable pageable);

    @DeleteMapping("trips/{id}")
    @Operation(summary = "Delete trip", description = "Delete trip")
    ResponseEntity<SuccessfulResponse> deleteTrip(@PathVariable Long id);

    @PutMapping("trips")
    @Operation(summary = "Update trip", description = "Update trip")
    TripDto updateTrip(@RequestBody @Valid TripUpdateRequest tripUpdateRequest);
}
