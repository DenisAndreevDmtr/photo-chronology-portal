package com.andersen.pc.portal.controller;

import com.andersen.pc.boot.PhotoChronologyApplication;
import com.andersen.pc.common.model.UserRole;
import com.andersen.pc.common.model.dto.request.TripCreationRequest;
import com.andersen.pc.common.model.dto.request.TripUpdateRequest;
import com.andersen.pc.common.model.entity.Token;
import com.andersen.pc.portal.security.factory.JwtAuthenticationFactory;
import com.andersen.pc.portal.security.jwt.AccessJwtProvider;
import com.andersen.pc.portal.security.jwt.JwtAuthentication;
import com.andersen.pc.portal.service.S3PhotoService;
import com.andersen.pc.portal.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = {
        PhotoChronologyApplication.class,
        TokenService.class,
        S3PhotoService.class

})
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@AutoConfigureWebMvc
@MockBeans({
        @MockBean(TokenService.class),
        @MockBean(AccessJwtProvider.class),
        @MockBean(JwtAuthenticationFactory.class),
        @MockBean(S3PhotoService.class)
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class TripControllerImplTest {

    private final MockMvc mockMvc;
    private final TokenService tokenService;
    private final AccessJwtProvider accessJwtProvider;
    private final JwtAuthenticationFactory jwtAuthenticationFactory;
    private final S3PhotoService s3PhotoService;
    private final static String BEARER = "Bearer token";
    private final static Long TRIP_ID = 1L;
    private final static Long USER_ID = 1L;

    @Nested
    class createTrip_should {
        @Test
        void success() throws Exception {
            JwtAuthentication authentication = createAuthentication();
            Token token = createActiveToken();
            TripCreationRequest tripCreationRequest = createTripCreationRequest();

            mockSecurity(token, authentication);

            mockMvc.perform(MockMvcRequestBuilders.post("/trips/creation")
                            .header(HttpHeaders.AUTHORIZATION, BEARER)
                            .content(convertObjectToJson(tripCreationRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpectAll(jsonPath("date")
                                    .value(tripCreationRequest.date().toString()),
                            jsonPath("title").value(tripCreationRequest.title()));
        }

        @Test
        void fail_invalidDto() throws Exception {
            JwtAuthentication authentication = createAuthentication();
            Token token = createActiveToken();
            TripCreationRequest tripCreationRequest = createInvalidTripCreationRequest();

            mockSecurity(token, authentication);

            mockMvc.perform(MockMvcRequestBuilders.post("/trips/creation")
                            .header(HttpHeaders.AUTHORIZATION, BEARER)
                            .content(convertObjectToJson(tripCreationRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        void fail_forbidden() throws Exception {
            JwtAuthentication authentication = createForbiddenAuthentication();
            Token token = createActiveToken();
            TripCreationRequest tripCreationRequest = createTripCreationRequest();

            mockSecurity(token, authentication);

            mockMvc.perform(MockMvcRequestBuilders.post("/trips/creation")
                            .header(HttpHeaders.AUTHORIZATION, BEARER)
                            .content(convertObjectToJson(tripCreationRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        void fail_unauthorized() throws Exception {
            TripCreationRequest tripCreationRequest = createTripCreationRequest();

            mockMvc.perform(MockMvcRequestBuilders.post("/trips/creation")
                            .content(convertObjectToJson(tripCreationRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        }
    }

    @Nested
    class getTripById_Should {
        @Test
        void success() throws Exception {
            JwtAuthentication authentication = createAuthentication();
            Token token = createActiveToken();

            mockSecurity(token, authentication);

            mockMvc.perform(MockMvcRequestBuilders.get("/trips/{id}", TRIP_ID)
                            .header(HttpHeaders.AUTHORIZATION, BEARER)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpectAll(jsonPath("id").value(TRIP_ID),
                            jsonPath("title").value("title"));
        }

        @Test
        void fail_tripNotFound() throws Exception {
            Long tripId = 999L;
            JwtAuthentication authentication = createAuthentication();
            Token token = createActiveToken();

            mockSecurity(token, authentication);

            mockMvc.perform(MockMvcRequestBuilders.get("/trips/{id}", tripId)
                            .header(HttpHeaders.AUTHORIZATION, BEARER)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        @Test
        void fail_forbidden() throws Exception {
            JwtAuthentication authentication = createForbiddenAuthentication();
            Token token = createActiveToken();

            mockSecurity(token, authentication);

            mockMvc.perform(MockMvcRequestBuilders.get("/trips/{id}", TRIP_ID)
                            .header(HttpHeaders.AUTHORIZATION, BEARER)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        void fail_unauthorized() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/trips/{id}", TRIP_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        }
    }

    @Nested
    class deleteTrip_Should {
        @Test
        void success() throws Exception {
            Long tripId = 3L;
            JwtAuthentication authentication = createAuthentication();
            Token token = createActiveToken();

            mockSecurity(token, authentication);
            doNothing().when(s3PhotoService).deletePhotoByKeyList(anyList());

            mockMvc.perform(MockMvcRequestBuilders.delete("/trips/{id}", tripId)
                            .header(HttpHeaders.AUTHORIZATION, BEARER)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        @Test
        void fail_tripNotFound() throws Exception {
            Long tripId = 999L;
            JwtAuthentication authentication = createAuthentication();
            Token token = createActiveToken();

            mockSecurity(token, authentication);

            mockMvc.perform(MockMvcRequestBuilders.delete("/trips/{id}", tripId)
                            .header(HttpHeaders.AUTHORIZATION, BEARER)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isConflict());
        }

        @Test
        void fail_forbidden() throws Exception {
            Long tripId = 3L;
            JwtAuthentication authentication = createForbiddenAuthentication();
            Token token = createActiveToken();

            mockSecurity(token, authentication);
            doNothing().when(s3PhotoService).deletePhotoByKeyList(anyList());

            mockMvc.perform(MockMvcRequestBuilders.delete("/trips/{id}", tripId)
                            .header(HttpHeaders.AUTHORIZATION, BEARER)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        void fail_unauthorized() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/trips/{id}", TRIP_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        }
    }

    @Nested
    class updateTrip_Should {
        @Test
        void success() throws Exception {
            JwtAuthentication authentication = createAuthentication();
            Token token = createActiveToken();
            TripUpdateRequest tripUpdateRequest = createTripUpdateRequest();

            mockSecurity(token, authentication);

            mockMvc.perform(MockMvcRequestBuilders.put("/trips")
                            .header(HttpHeaders.AUTHORIZATION, BEARER)
                            .content(convertObjectToJson(tripUpdateRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpectAll(jsonPath("date")
                                    .value(tripUpdateRequest.dateUpdate().toString()),
                            jsonPath("title").value(tripUpdateRequest.titleUpdate()));
        }

        @Test
        void fail_tripNotFound() throws Exception {
            JwtAuthentication authentication = createAuthentication();
            Token token = createActiveToken();
            TripUpdateRequest tripUpdateRequest = createTripUpdateRequestWithInvalidId();

            mockSecurity(token, authentication);

            mockMvc.perform(MockMvcRequestBuilders.put("/trips")
                            .header(HttpHeaders.AUTHORIZATION, BEARER)
                            .content(convertObjectToJson(tripUpdateRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isConflict());
        }

        @Test
        void fail_invalidDto() throws Exception {
            JwtAuthentication authentication = createAuthentication();
            Token token = createActiveToken();
            TripUpdateRequest tripUpdateRequest = createInvalidTripUpdateRequest();

            mockSecurity(token, authentication);

            mockMvc.perform(MockMvcRequestBuilders.put("/trips")
                            .header(HttpHeaders.AUTHORIZATION, BEARER)
                            .content(convertObjectToJson(tripUpdateRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        void fail_forbidden() throws Exception {
            JwtAuthentication authentication = createForbiddenAuthentication();
            Token token = createActiveToken();
            TripUpdateRequest tripUpdateRequest = createTripUpdateRequest();

            mockSecurity(token, authentication);

            mockMvc.perform(MockMvcRequestBuilders.put("/trips")
                            .header(HttpHeaders.AUTHORIZATION, BEARER)
                            .content(convertObjectToJson(tripUpdateRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        void fail_unauthorized() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/trips/{id}", TRIP_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        }
    }

    @Nested
    class searchUserTrips_Should {
        @Test
        void success() throws Exception {
            JwtAuthentication authentication = createAuthentication();
            Token token = createActiveToken();

            mockSecurity(token, authentication);

            mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}/trips", USER_ID)
                            .header(HttpHeaders.AUTHORIZATION, BEARER)
                            .param("title", "search")
                            .param("dateFrom", "")
                            .param("dateTo", "")
                            .param("page", "1")
                            .param("size", "10")
                            .param("sort", "id,asc")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(jsonPath("$.totalCount").value(1L))
                    .andExpect(jsonPath("$.requestResult").isArray())
                    .andExpect(jsonPath("$.requestResult.length()").value(1))
                    .andExpect(jsonPath("$.requestResult[0].id").value(2L))
                    .andExpect(jsonPath("$.requestResult[0].title").value("trip for search"));
        }

        @Test
        void success_withoutSearchParameters() throws Exception {
            JwtAuthentication authentication = createAuthentication();
            Token token = createActiveToken();

            mockSecurity(token, authentication);

            mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}/trips", USER_ID)
                            .header(HttpHeaders.AUTHORIZATION, BEARER)
                            .param("page", "1")
                            .param("size", "10")
                            .param("sort", "id,asc")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(jsonPath("$.totalCount").value(4L))
                    .andExpect(jsonPath("$.requestResult").isArray())
                    .andExpect(jsonPath("$.requestResult.length()").value(4));
        }

        @Test
        void fail_invalidSearchParameters() throws Exception {
            JwtAuthentication authentication = createAuthentication();
            Token token = createActiveToken();

            mockSecurity(token, authentication);

            mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}/trips", USER_ID)
                            .header(HttpHeaders.AUTHORIZATION, BEARER)
                            .param("title", "s")
                            .param("dateFrom", "")
                            .param("dateTo", "")
                            .param("page", "1")
                            .param("size", "10")
                            .param("sort", "id,asc")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        void fail_forbidden() throws Exception {
            JwtAuthentication authentication = createForbiddenAuthentication();
            Token token = createActiveToken();

            mockSecurity(token, authentication);

            mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}/trips", USER_ID)
                            .header(HttpHeaders.AUTHORIZATION, BEARER)
                            .param("title", "search")
                            .param("dateFrom", "")
                            .param("dateTo", "")
                            .param("page", "1")
                            .param("size", "10")
                            .param("sort", "id,asc")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        void fail_unauthorized() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}/trips", USER_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        }
    }

    private TripCreationRequest createTripCreationRequest() {
        return new TripCreationRequest(
                "new title",
                LocalDate.now());
    }

    private TripCreationRequest createInvalidTripCreationRequest() {
        return new TripCreationRequest(
                "new title",
                LocalDate.MAX);
    }

    private TripUpdateRequest createTripUpdateRequest() {
        return new TripUpdateRequest(
                "updated title",
                LocalDate.of(2024, 1, 1),
                4L);
    }


    private TripUpdateRequest createInvalidTripUpdateRequest() {
        return new TripUpdateRequest(
                "updated title",
                LocalDate.of(2024, 1, 1),
                null);
    }

    private TripUpdateRequest createTripUpdateRequestWithInvalidId() {
        return new TripUpdateRequest(
                "updated title",
                LocalDate.of(2024, 1, 1),
                99L);
    }

    private JwtAuthentication createAuthentication() {
        JwtAuthentication authentication = new JwtAuthentication();
        authentication.setAuthenticated(true);
        authentication.setRoles(Set.of(UserRole.USER, UserRole.ADMIN));
        authentication.setUserId(1L);
        authentication.setUserName("admin");
        authentication.setEmail("admin@email.com");
        return authentication;
    }

    private JwtAuthentication createForbiddenAuthentication() {
        JwtAuthentication authentication = new JwtAuthentication();
        authentication.setAuthenticated(true);
        authentication.setRoles(Set.of(UserRole.TEST));
        authentication.setUserId(1L);
        authentication.setUserName("admin");
        authentication.setEmail("admin@email.com");
        return authentication;
    }

    private Token createActiveToken() {
        Token token = new Token();
        token.setToken("token");
        token.setIsActive(Boolean.TRUE);
        return token;
    }

    private void mockSecurity(Token token, JwtAuthentication authentication) {
        when(tokenService.findByToken(anyString())).thenReturn(Optional.of(token));
        when(accessJwtProvider.validateToken(anyString())).thenReturn(true);
        when(jwtAuthenticationFactory.create(any())).thenReturn(authentication);
    }

    public static String convertObjectToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(object);
    }
}