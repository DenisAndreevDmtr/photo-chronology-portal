package com.andersen.pc.common.controller;

import com.andersen.pc.common.model.dto.request.UserCreationRequest;
import com.andersen.pc.common.model.dto.request.UserPasswordResetRequest;
import com.andersen.pc.common.model.dto.request.UserSearchRequest;
import com.andersen.pc.common.model.dto.request.UserUpdateRequest;
import com.andersen.pc.common.model.dto.response.RequestResult;
import com.andersen.pc.common.model.dto.response.SuccessfulResponse;
import com.andersen.pc.common.model.dto.response.UserDto;
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
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User", description = "User's management")
@RequestMapping("users")
@SecurityRequirement(name = "bearerAuth")
public interface UserController {

    @Operation(summary = "Create new user", description = "Create new user")
    @PostMapping("/creation")
    UserDto createUser(@RequestBody @Valid UserCreationRequest userCreationRequest);


    @Operation(summary = "Get user by id", description = "Get user by id")
    @GetMapping("/{id}")
    UserDto getUser(@PathVariable Long id);

    @Operation(summary = "Delete user", description = "Delete user")
    @DeleteMapping("/{id}")
    ResponseEntity<SuccessfulResponse> deleteUser(@PathVariable Long id);

    @Operation(summary = "Update user", description = "Update user")
    @PutMapping()
    UserDto updateUser(@RequestBody @Valid UserUpdateRequest userUpdateRequest);

    @Operation(summary = "Change user password", description = "Change user password")
    @PutMapping("/{id}/password")
    ResponseEntity<SuccessfulResponse> chaneUserPassword(
            @RequestBody @Valid UserPasswordResetRequest userPasswordResetRequest,
            @PathVariable Long id);

    @Operation(summary = "Deactivate user", description = "Deactivate user")
    @PutMapping("/{id}/deactivation")
    UserDto deactivateUser(@PathVariable Long id);

    @Operation(summary = "Get list of users by search and sort parameters",
            description = "Get list of users by search and sort parameters",
            parameters = {
                    @Parameter(in = ParameterIn.QUERY,
                            description = """
                                    Sorting criteria in the format: property,(asc|desc). Default sorting is id,ask.
                                    Multiple sort criteria are supported.<br />
                                    <ul>For sorting
                                        <li>by id: id;</li>
                                        <li>by name: name;</li>
                                        <li>by email: email;</li>
                                    </ul>""",
                            name = "sort",
                            array = @ArraySchema(schema = @Schema(type = "string")))
            })
    @GetMapping()
    RequestResult getUsersBySearchParameters(
            @Valid UserSearchRequest userSearchRequest,
            @SortDefault(sort = "id") Pageable pageable);
}
