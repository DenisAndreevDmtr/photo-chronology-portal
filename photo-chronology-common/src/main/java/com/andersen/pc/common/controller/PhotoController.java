package com.andersen.pc.common.controller;

import com.andersen.pc.common.model.dto.request.PhotoCreationRequest;
import com.andersen.pc.common.model.dto.request.PhotoUpdateRequest;
import com.andersen.pc.common.model.dto.response.PhotoDto;
import com.andersen.pc.common.model.dto.response.SuccessfulResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Photo", description = "Photo's management")
@RequestMapping("photos")
@SecurityRequirement(name = "bearerAuth")
public interface PhotoController {

    @PostMapping()
    @Operation(summary = "Create new photo", description = "Create new photo")
    PhotoDto createPhoto(
            @RequestPart(value = "body") @Parameter(schema = @Schema(type = "string", format = "binary"))
            @Valid PhotoCreationRequest photoCreationRequest,
            @RequestPart(value = "file") MultipartFile file);

    @GetMapping("/{id}")
    @Operation(summary = "Get photo by id", description = "Get photo by id")
    PhotoDto getPhotoById(@PathVariable Long id);

    @GetMapping("{id}/link")
    @Operation(summary = "Get photo link by photo id", description = "Get photo link by photo id")
    String getPhotoLinkById(@PathVariable Long id);

    @PutMapping()
    @Operation(description = "Update photo")
    PhotoDto updatePhoto(@RequestPart(value = "body") @Parameter(schema = @Schema(type = "string", format = "binary"))
                         @Valid PhotoUpdateRequest photoUpdateRequest,
                         @RequestPart(value = "file") MultipartFile file);

    @DeleteMapping("{id}")
    @Operation(summary = "Delete photo by id", description = "Delete photo by id")
    ResponseEntity<SuccessfulResponse> deletePhoto(@PathVariable Long id);
}
