package com.andersen.pc.portal.factory;

import com.andersen.pc.common.model.dto.request.TripCreationRequest;
import com.andersen.pc.common.model.dto.request.TripUpdateRequest;
import com.andersen.pc.common.model.dto.response.TripDto;
import com.andersen.pc.common.model.entity.Trip;
import com.andersen.pc.common.model.entity.User;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

public class TripFactory {

    public static final Long TRIP_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final String TITLE = "title";
    private static final String TITLE_UPDATE = "title update";
    private static final LocalDate DATE = LocalDate.of(2024, 1, 1);

    public static Trip getEntityObject() {
        User user = UserFactory.getEntityObject();
        return Trip.builder()
                .id(TRIP_ID)
                .title(TITLE)
                .date(LocalDate.now())
                .user(user)
                .build();
    }

    public static TripCreationRequest getTripCreationRequest() {
        return new TripCreationRequest(TITLE, DATE);
    }

    public static TripUpdateRequest getTripUpdateRequest() {
        return new TripUpdateRequest(TITLE_UPDATE, DATE, TRIP_ID);
    }

    public static TripUpdateRequest getTripUpdateReqWithoutUpdates() {
        return new TripUpdateRequest(TITLE, DATE, TRIP_ID);
    }

    public static TripDto getTripDto() {
        return new TripDto(
                TRIP_ID,
                DATE,
                TITLE,
                USER_ID,
                Collections.emptySet(),
                null,
                null,
                null,
                null);
    }
}
