package com.andersen.pc.portal.service;

import com.andersen.pc.common.exception.DbObjectConflictException;
import com.andersen.pc.common.exception.DbObjectNotFoundException;
import com.andersen.pc.common.model.dto.request.TripCreationRequest;
import com.andersen.pc.common.model.dto.request.TripUpdateRequest;
import com.andersen.pc.common.model.dto.response.TripDto;
import com.andersen.pc.common.model.entity.Trip;
import com.andersen.pc.common.model.entity.User;
import com.andersen.pc.portal.factory.JwtAuthenticationFactory;
import com.andersen.pc.portal.factory.TripFactory;
import com.andersen.pc.portal.factory.UserFactory;
import com.andersen.pc.portal.mappers.TripMapper;
import com.andersen.pc.portal.repository.PhotoRepository;
import com.andersen.pc.portal.repository.TripRepository;
import com.andersen.pc.portal.security.jwt.JwtAuthentication;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import static com.andersen.pc.common.constant.Constant.Errors.DONT_HAVE_PERMISSION_FOR_THAT;
import static com.andersen.pc.common.constant.Constant.Errors.TRIP_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {
    @InjectMocks
    TripService tripService;

    @Mock
    private UserService userService;
    @Mock
    private TripMapper tripMapper;
    @Mock
    private TripRepository tripRepository;
    @Mock
    private S3PhotoService s3PhotoService;
    @Mock
    private PhotoRepository photoRepository;

    @Nested
    class createTrip_should {

        @Test
        void success() {
            Trip trip = TripFactory.getEntityObject();
            User user = UserFactory.getEntityObject();
            TripCreationRequest tripCreationRequest = TripFactory.getTripCreationRequest();
            JwtAuthentication jwtAuthentication = JwtAuthenticationFactory.getObject();
            TripDto expected = TripFactory.getTripDto();

            when(userService.getUserByEmail(any())).thenReturn(user);
            when(tripMapper.dtoToData(any(), any())).thenReturn(trip);
            when(tripRepository.save(any())).thenReturn(trip);
            when(tripMapper.dataToDto(any())).thenReturn(expected);

            TripDto actual = tripService.createTrip(tripCreationRequest, jwtAuthentication);

            assertEquals(expected, actual);
            verify(userService).getUserByEmail(any());
            verify(tripMapper).dtoToData(any(), any());
            verify(tripRepository).save(any());
            verify(tripMapper).dataToDto(any());
        }
    }

    @Nested
    class getTripById_should {

        @Test
        void success() {
            Trip trip = TripFactory.getEntityObject();
            TripDto expected = TripFactory.getTripDto();

            when(tripRepository.findById(any())).thenReturn(Optional.of(trip));
            when(tripMapper.dataToDto(any())).thenReturn(expected);

            TripDto actual = tripService.getTripById(TripFactory.TRIP_ID);

            assertEquals(expected, actual);
            verify(tripRepository).findById(any());
            verify(tripMapper).dataToDto(any());
        }

        @Test
        void fail_tripNotFound() {
            doThrow(new DbObjectConflictException(TRIP_NOT_FOUND))
                    .when(tripRepository).findById(any());

            assertThrows(
                    DbObjectNotFoundException.class,
                    () -> tripService.getTripById(1L),
                    TRIP_NOT_FOUND
            );
        }
    }

    @Nested
    class getById_should {

        @Test
        void success() {
            Trip trip = TripFactory.getEntityObject();
            TripDto expected = TripFactory.getTripDto();

            when(tripRepository.findById(any())).thenReturn(Optional.of(trip));
            when(tripMapper.dataToDto(any())).thenReturn(expected);

            TripDto actual = tripService.getTripById(TripFactory.TRIP_ID);

            assertEquals(expected, actual);
            verify(tripRepository).findById(any());
            verify(tripMapper).dataToDto(any());
        }

        @Test
        void fail_tripNotFound() {
            when(tripRepository.findById(any())).thenReturn(Optional.empty());

            assertThrows(
                    DbObjectNotFoundException.class,
                    () -> tripService.getTripById(TripFactory.TRIP_ID),
                    TRIP_NOT_FOUND
            );
        }
    }

    @Nested
    class updateTrip_should {

        @Test
        void success() {
            Trip trip = TripFactory.getEntityObject();
            TripUpdateRequest updateRequest = TripFactory.getTripUpdateRequest();
            JwtAuthentication jwtAuthentication = JwtAuthenticationFactory.getObject();
            TripDto expected = TripFactory.getTripDto();

            when(tripRepository.findById(any())).thenReturn(Optional.of(trip));
            when(tripRepository.save(any())).thenReturn(trip);
            when(tripMapper.dataToDto(any())).thenReturn(expected);

            TripDto actual = tripService.updateTrip(updateRequest, jwtAuthentication);

            assertEquals(expected, actual);
            verify(tripRepository).findById(any());
            verify(tripRepository).save(any());
            verify(tripMapper).dataToDto(any());
        }

        @Test
        void success_noUpdates() {
            Trip trip = TripFactory.getEntityObject();
            TripUpdateRequest updateRequest = TripFactory.getTripUpdateRequest();
            JwtAuthentication jwtAuthentication = JwtAuthenticationFactory.getObject();
            TripDto expected = TripFactory.getTripDto();

            when(tripRepository.findById(any())).thenReturn(Optional.of(trip));
            when(tripRepository.save(any())).thenReturn(trip);
            when(tripMapper.dataToDto(any())).thenReturn(expected);

            TripDto actual = tripService.updateTrip(updateRequest, jwtAuthentication);

            assertEquals(expected, actual);
            verify(tripRepository).findById(any());
            verifyNoMoreInteractions(tripRepository);
            verify(tripMapper).dataToDto(any());
        }

        @Test
        void fail_userIsNotDataOwner() {
            TripUpdateRequest updateRequest = TripFactory.getTripUpdateRequest();
            JwtAuthentication jwtAuthentication = JwtAuthenticationFactory.getObject();
            jwtAuthentication.setUserId(2L);
            Trip trip = TripFactory.getEntityObject();

            when(tripRepository.findById(any())).thenReturn(Optional.of(trip));

            assertThrows(
                    AccessDeniedException.class,
                    () -> tripService.updateTrip(updateRequest, jwtAuthentication),
                    DONT_HAVE_PERMISSION_FOR_THAT
            );
        }
    }

    @Nested
    class deleteByTripId_should {

        @Test
        void success() {
            Trip trip = TripFactory.getEntityObject();
            JwtAuthentication jwtAuthentication = JwtAuthenticationFactory.getObject();

            when(tripRepository.findById(any())).thenReturn(Optional.of(trip));
            when(photoRepository.findAwsKeysByTripId(any())).thenReturn(List.of("key"));
            doNothing().when(s3PhotoService).deletePhotoByKeyList(anyList());
            doNothing().when(tripRepository).deleteById(any());

            tripService.deleteByTripId(TripFactory.TRIP_ID, jwtAuthentication);

            verify(tripRepository).findById(any());
            verify(photoRepository).findAwsKeysByTripId(any());
            verify(s3PhotoService).deletePhotoByKeyList(anyList());
            verify(tripRepository).deleteById(any());
        }

        @Test
        void fail_userIsNotDataOwner() {
            JwtAuthentication jwtAuthentication = JwtAuthenticationFactory.getObject();
            jwtAuthentication.setUserId(2L);
            Trip trip = TripFactory.getEntityObject();

            when(tripRepository.findById(any())).thenReturn(Optional.of(trip));

            assertThrows(
                    AccessDeniedException.class,
                    () -> tripService.deleteByTripId(TripFactory.TRIP_ID, jwtAuthentication),
                    DONT_HAVE_PERMISSION_FOR_THAT
            );
        }
    }
}