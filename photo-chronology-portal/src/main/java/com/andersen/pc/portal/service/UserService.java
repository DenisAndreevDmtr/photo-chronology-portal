package com.andersen.pc.portal.service;

import com.andersen.pc.common.exception.DbObjectConflictException;
import com.andersen.pc.common.exception.DbObjectNotFoundException;
import com.andersen.pc.common.model.UserStatus;
import com.andersen.pc.common.model.dto.request.UserCreationRequest;
import com.andersen.pc.common.model.dto.request.UserSearchRequest;
import com.andersen.pc.common.model.dto.request.UserUpdateRequest;
import com.andersen.pc.common.model.dto.response.UserDto;
import com.andersen.pc.common.model.entity.User;
import com.andersen.pc.portal.mappers.UserMapper;
import com.andersen.pc.portal.repository.PhotoRepository;
import com.andersen.pc.portal.repository.UserRepository;
import com.andersen.pc.portal.repository.UserSearchRepository;
import com.andersen.pc.portal.security.jwt.JwtAuthentication;
import com.andersen.pc.portal.utils.SortParameterValidator;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import static com.andersen.pc.common.constant.Constant.Errors.DONT_HAVE_PERMISSION_FOR_THAT;
import static com.andersen.pc.common.constant.Constant.Errors.USER_NOT_FOUND;
import static com.andersen.pc.common.constant.Constant.Errors.USER_WITH_SUCH_EMAIL_IS_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TokenService tokenService;
    private final UserPasswordService userPasswordService;
    private final UserRoleService userRoleService;
    private final UserSearchRepository userSearchRepository;
    private final PhotoRepository photoRepository;
    private final S3PhotoService s3PhotoService;


    public User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new DbObjectConflictException(USER_NOT_FOUND));
    }

    @Transactional
    public UserDto createUser(UserCreationRequest userCreationRequest) {
        validateNewUser(userCreationRequest);
        User user = userMapper.dtoToData(userCreationRequest);
        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);
        char[] password = userCreationRequest.password();
        userPasswordService.createPasswordForUser(user, password);
        user = userRoleService.createUserRole(user);
        return userMapper.dataToDto(user);
    }

    public UserDto findUserById(Long id) {
        try {
            return getUserById(id);
        } catch (DbObjectConflictException exception) {
            throw new DbObjectNotFoundException(USER_NOT_FOUND);
        }
    }

    private UserDto getUserById(Long id) {
        User user = findById(id);
        return userMapper.dataToDto(user);
    }

    public void deleteUserById(Long id) {
        User user = findById(id);
        processPhotoData(user);
        userRepository.delete(user);
    }

    public Pair<Long, List<UserDto>> getUsersBySearchParameters(
            UserSearchRequest userSearchRequest,
            Pageable pageable) {
        SortParameterValidator.checkSortParameter(User.class, pageable);
        QueryResults<User> result = userSearchRepository.getUsersBySearchParameter(userSearchRequest, pageable);
        List<UserDto> usersDto = userMapper.dataListToDtoList(result.getResults());
        return new ImmutablePair<>(result.getTotal(), usersDto);
    }

    @Transactional
    public UserDto deactivateUserById(Long id) {
        User user = findById(id);
        if (!UserStatus.DISABLED.equals(user.getStatus())) {
            user.setStatus(UserStatus.DISABLED);
            user = userRepository.save(user);
            tokenService.deleteTokensByUserId(id);
        }
        return userMapper.dataToDto(user);
    }

    public UserDto updateUser(
            UserUpdateRequest userUpdateRequest,
            JwtAuthentication authentication) {
        User user = userRepository.findById(userUpdateRequest.userId())
                .orElseThrow(() -> new DbObjectConflictException(USER_NOT_FOUND));
        validateUserIsDataOwner(user, authentication);
        boolean isNameUpdated = isUserNameUpdated(user, userUpdateRequest);
        boolean isEmailUpdated = isUserEmailUpdated(user, userUpdateRequest);
        if (isNameUpdated || isEmailUpdated) {
            user = userRepository.save(user);
        }
        return userMapper.dataToDto(user);
    }

    private boolean isUserNameUpdated(User user, UserUpdateRequest userUpdate) {
        boolean isUserUpdated = false;
        if (StringUtils.isNotEmpty(userUpdate.nameUpdate()) && !userUpdate.nameUpdate().equals(user.getName())) {
            user.setName(userUpdate.nameUpdate());
            isUserUpdated = true;
        }
        return isUserUpdated;
    }

    private boolean isUserEmailUpdated(User user, UserUpdateRequest userUpdate) {
        boolean isUserUpdated = false;
        if (StringUtils.isNotEmpty(userUpdate.nameUpdate()) && !userUpdate.nameUpdate().equals(user.getName())) {
            user.setEmail(userUpdate.emailUpdate());
            isUserUpdated = true;
        }
        return isUserUpdated;
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DbObjectConflictException(USER_NOT_FOUND));
    }

    private void validateNewUser(UserCreationRequest userCreationRequest) {
        String email = userCreationRequest.email();
        Optional<User> existingUser = userRepository.findByEmailIgnoreCase(email);
        if (existingUser.isPresent()) {
            throw new DbObjectConflictException(USER_WITH_SUCH_EMAIL_IS_ALREADY_EXISTS);
        }
    }

    @SneakyThrows
    private void validateUserIsDataOwner(User user, JwtAuthentication authentication) {
        if (!authentication.isAdmin()
                && !user.getId().equals(authentication.getUserId())) {
            throw new AccessDeniedException(DONT_HAVE_PERMISSION_FOR_THAT);
        }
    }

    private void processPhotoData(User user) {
        List<String> photos = photoRepository.findAwsKeysByUserId(user.getId());
        s3PhotoService.deletePhotoByKeyList(photos);
    }
}
