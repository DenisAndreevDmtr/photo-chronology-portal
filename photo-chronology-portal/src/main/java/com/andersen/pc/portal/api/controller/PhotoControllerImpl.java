package com.andersen.pc.portal.api.controller;

import com.andersen.pc.common.controller.PhotoController;
import com.andersen.pc.common.model.dto.request.PhotoCreationRequest;
import com.andersen.pc.common.model.dto.request.PhotoUpdateRequest;
import com.andersen.pc.common.model.dto.response.PhotoDto;
import com.andersen.pc.common.model.dto.response.SuccessfulResponse;
import com.andersen.pc.portal.security.jwt.JwtAuthentication;
import com.andersen.pc.portal.service.AuthenticationService;
import com.andersen.pc.portal.service.PhotoService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class PhotoControllerImpl implements PhotoController {

    private final PhotoService photoService;
    private final AuthenticationService authenticationService;

    @Override
    @PreAuthorize("hasAnyAuthority(T(com.andersen.pc.common.model.UserRole).ADMIN," +
            " T(com.andersen.pc.common.model.UserRole).USER)")
    public PhotoDto createPhoto(
            @RequestPart(value = "body") @Parameter(schema = @Schema(type = "string", format = "binary"))
            @Valid PhotoCreationRequest photoCreationRequest,
            @RequestPart(value = "file") MultipartFile file) {
        JwtAuthentication authentication = authenticationService.getAuthentication();
        return photoService.createPhoto(file, photoCreationRequest, authentication);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(T(com.andersen.pc.common.model.UserRole).ADMIN," +
            " T(com.andersen.pc.common.model.UserRole).USER)")
    public PhotoDto getPhotoById(@PathVariable Long id) {
        return photoService.findPhotoById(id);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(T(com.andersen.pc.common.model.UserRole).ADMIN," +
            " T(com.andersen.pc.common.model.UserRole).USER)")
    public String getPhotoLinkById(@PathVariable Long id) {
        return photoService.getPhotoLink(id);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(T(com.andersen.pc.common.model.UserRole).ADMIN," +
            " T(com.andersen.pc.common.model.UserRole).USER)")
    public PhotoDto updatePhoto(@RequestPart(value = "body") @Parameter(schema = @Schema(type = "string", format = "binary"))
                                @Valid PhotoUpdateRequest photoUpdateRequest,
                                @RequestPart(value = "file") MultipartFile file) {
        JwtAuthentication authentication = authenticationService.getAuthentication();
        return photoService.updatePhoto(file, photoUpdateRequest, authentication);
    }

    @Override
    @PreAuthorize("hasAuthority(T(com.andersen.pc.common.model.UserRole).ADMIN) || " +
            "hasAuthority(T(com.andersen.pc.common.model.UserRole).USER)")
    public ResponseEntity<SuccessfulResponse> deletePhoto(@PathVariable Long id) {
        JwtAuthentication authentication = authenticationService.getAuthentication();
        photoService.deletePhotoById(id, authentication);
        return ResponseEntity.ok(new SuccessfulResponse(Boolean.TRUE));
    }
}
