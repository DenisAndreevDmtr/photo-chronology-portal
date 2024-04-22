package com.andersen.pc.portal.service;

import com.andersen.pc.common.exception.DbObjectConflictException;
import com.andersen.pc.common.exception.DbObjectNotFoundException;
import com.andersen.pc.common.model.dto.request.TripCreationRequest;
import com.andersen.pc.common.model.dto.request.TripSearchRequest;
import com.andersen.pc.common.model.dto.request.TripUpdateRequest;
import com.andersen.pc.common.model.dto.response.TripDto;
import com.andersen.pc.common.model.entity.Trip;
import com.andersen.pc.common.model.entity.User;
import com.andersen.pc.portal.mappers.TripMapper;
import com.andersen.pc.portal.repository.PhotoRepository;
import com.andersen.pc.portal.repository.TripRepository;
import com.andersen.pc.portal.repository.TripSearchSpecification;
import com.andersen.pc.portal.security.jwt.JwtAuthentication;
import com.andersen.pc.portal.utils.SortParameterValidator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;

import static com.andersen.pc.common.constant.Constant.Errors.DONT_HAVE_PERMISSION_FOR_THAT;
import static com.andersen.pc.common.constant.Constant.Errors.TRIP_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TripService {

    private final UserService userService;
    private final TripMapper tripMapper;
    private final TripRepository tripRepository;
    private final S3PhotoService s3PhotoService;
    private final PhotoRepository photoRepository;

    public TripDto createTrip(TripCreationRequest tripCreationRequest, JwtAuthentication authentication) {
        User user = userService.getUserByEmail(authentication.getEmail());
        Trip trip = tripMapper.dtoToData(tripCreationRequest, user);
        Trip savedTrip = tripRepository.save(trip);
        return tripMapper.dataToDto(savedTrip);
    }

    public TripDto getTripById(Long id) {
        try {
            return findById(id);
        } catch (DbObjectConflictException exception) {
            throw new DbObjectNotFoundException(TRIP_NOT_FOUND);
        }
    }

    public Trip getById(Long id) {
        return tripRepository.findById(id).
                orElseThrow(() -> new DbObjectConflictException(TRIP_NOT_FOUND));
    }

    public Pair<Long, List<TripDto>> getTripsBySearchParameters(
            TripSearchRequest tripSearchRequest,
            Long userId,
            Pageable pageable) {
        SortParameterValidator.checkSortParameter(Trip.class, pageable);
        TripSearchSpecification specification = new TripSearchSpecification(tripSearchRequest, userId);
        Page<Trip> page = tripRepository.findAll(specification, pageable);
        List<TripDto> tripsDto = tripMapper.dataListToDtoList(page.getContent());
        return new ImmutablePair<>(page.getTotalElements(), tripsDto);
    }

    public TripDto updateTrip(
            TripUpdateRequest tripUpdateRequest,
            JwtAuthentication authentication) {
        Trip trip = getById(tripUpdateRequest.tripId());
        validateUserIsDataOwner(trip, authentication);
        boolean isTitleUpdated = isTitleUpdated(trip, tripUpdateRequest);
        boolean isDateUpdated = isDateUpdated(trip, tripUpdateRequest);
        if (isTitleUpdated || isDateUpdated) {
            trip = tripRepository.save(trip);
        }
        return tripMapper.dataToDto(trip);
    }

    public void deleteByTripId(Long id, JwtAuthentication authentication) {
        Trip trip = getById(id);
        validateUserIsDataOwner(trip, authentication);
        processPhotoData(trip);
        tripRepository.deleteById(id);
    }

    @SneakyThrows
    public void validateUserIsDataOwner(Trip trip, JwtAuthentication authentication) {
        if (!authentication.isAdmin()
                && !trip.getUser().getId().equals(authentication.getUserId())) {
            throw new AccessDeniedException(DONT_HAVE_PERMISSION_FOR_THAT);
        }
    }

    private boolean isTitleUpdated(Trip trip, TripUpdateRequest tripUpdateRequest) {
        boolean isTripUpdated = false;
        if (!StringUtils.isEmpty(tripUpdateRequest.titleUpdate())
                && !tripUpdateRequest.titleUpdate().equals(trip.getTitle())) {
            trip.setTitle(tripUpdateRequest.titleUpdate());
            isTripUpdated = true;
        }
        return isTripUpdated;
    }

    private boolean isDateUpdated(Trip trip, TripUpdateRequest tripUpdateRequest) {
        boolean isTripUpdated = false;
        if (Objects.nonNull(tripUpdateRequest.dateUpdate())
                && !tripUpdateRequest.dateUpdate().equals(trip.getDate())) {
            trip.setDate(tripUpdateRequest.dateUpdate());
            isTripUpdated = true;
        }
        return isTripUpdated;
    }

    private void processPhotoData(Trip trip) {
        List<String> photos = photoRepository.findAwsKeysByTripId(trip.getId());
        s3PhotoService.deletePhotoByKeyList(photos);
    }

    private TripDto findById(Long id) {
        Trip trip = getById(id);
        return tripMapper.dataToDto(trip);
    }
}
