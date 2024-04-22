package com.andersen.pc.portal.api.controller;

import com.andersen.pc.common.controller.TripController;
import com.andersen.pc.common.model.dto.request.TripCreationRequest;
import com.andersen.pc.common.model.dto.request.TripSearchRequest;
import com.andersen.pc.common.model.dto.request.TripUpdateRequest;
import com.andersen.pc.common.model.dto.response.RequestResult;
import com.andersen.pc.common.model.dto.response.SuccessfulResponse;
import com.andersen.pc.common.model.dto.response.TripDto;
import com.andersen.pc.portal.security.jwt.JwtAuthentication;
import com.andersen.pc.portal.service.AuthenticationService;
import com.andersen.pc.portal.service.TripService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Trip", description = "Trip's management")
public class TripControllerImpl implements TripController {

    private final AuthenticationService authenticationService;
    private final TripService tripService;

    @Override
    @PreAuthorize("hasAuthority(T(com.andersen.pc.common.model.UserRole).ADMIN) || " +
            "hasAuthority(T(com.andersen.pc.common.model.UserRole).USER)")
    public TripDto createTrip(@RequestBody @Valid TripCreationRequest tripCreationRequest) {
        JwtAuthentication authentication = authenticationService.getAuthentication();
        return tripService.createTrip(tripCreationRequest, authentication);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(T(com.andersen.pc.common.model.UserRole).ADMIN," +
            " T(com.andersen.pc.common.model.UserRole).USER)")
    public TripDto getTripById(@PathVariable Long id) {
        return tripService.getTripById(id);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(T(com.andersen.pc.common.model.UserRole).ADMIN," +
            " T(com.andersen.pc.common.model.UserRole).USER)")
    public RequestResult searchUserTrips(
            @Valid TripSearchRequest tripSearchRequest,
            @PathVariable Long id,
            @SortDefault(sort = "id") Pageable pageable) {
        Pair<Long, List<TripDto>> tripsDtoPairs = tripService.getTripsBySearchParameters(tripSearchRequest, id, pageable);
        return new RequestResult(tripsDtoPairs.getKey(), tripsDtoPairs.getValue());
    }

    @Override
    @PreAuthorize("hasAuthority(T(com.andersen.pc.common.model.UserRole).ADMIN) || " +
            "hasAuthority(T(com.andersen.pc.common.model.UserRole).USER)")
    public ResponseEntity<SuccessfulResponse> deleteTrip(@PathVariable Long id) {
        JwtAuthentication authentication = authenticationService.getAuthentication();
        tripService.deleteByTripId(id, authentication);
        return ResponseEntity.ok(new SuccessfulResponse(Boolean.TRUE));
    }

    @Override
    @PreAuthorize("hasAnyAuthority(T(com.andersen.pc.common.model.UserRole).ADMIN)")
    public TripDto updateTrip(@RequestBody @Valid TripUpdateRequest tripUpdateRequest) {
        JwtAuthentication authentication = authenticationService.getAuthentication();
        return tripService.updateTrip(tripUpdateRequest, authentication);
    }
}
