package com.andersen.pc.portal.service;

import com.andersen.pc.common.exception.DbObjectConflictException;
import com.andersen.pc.common.exception.DbObjectNotFoundException;
import com.andersen.pc.common.model.dto.request.PhotoCreationRequest;
import com.andersen.pc.common.model.dto.request.PhotoUpdateRequest;
import com.andersen.pc.common.model.dto.response.PhotoDto;
import com.andersen.pc.common.model.entity.Photo;
import com.andersen.pc.common.model.entity.Trip;
import com.andersen.pc.portal.mappers.PhotoMapper;
import com.andersen.pc.portal.repository.PhotoRepository;
import com.andersen.pc.portal.security.jwt.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

import static com.andersen.pc.common.constant.Constant.Errors.DONT_HAVE_PERMISSION_FOR_THAT;
import static com.andersen.pc.common.constant.Constant.Errors.PHOTO_NOT_FOUND;
import static com.andersen.pc.common.constant.Constant.Errors.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final PhotoMapper photoMapper;
    private final TripService tripService;
    private final S3PhotoService s3PhotoService;

    public PhotoDto findPhotoById(Long id) {
        try {
            return getPhotoById(id);
        } catch (DbObjectConflictException exception) {
            throw new DbObjectNotFoundException(USER_NOT_FOUND);
        }
    }

    public PhotoDto getPhotoById(Long id) {
        Photo photo = findById(id);
        return photoMapper.dataToDto(photo);
    }

    public String getPhotoLink(Long id) {
        Photo photo = findById(id);
        return s3PhotoService.generatePreSignedUrl(photo.getAwsKey());
    }

    @Transactional
    public PhotoDto createPhoto(
            MultipartFile multipartFile,
            PhotoCreationRequest photoCreationRequest,
            JwtAuthentication authentication) {
        Trip trip = tripService.getById(photoCreationRequest.tripId());
        tripService.validateUserIsDataOwner(trip, authentication);
        Photo photo = new Photo();
        photo.setTitle(photoCreationRequest.title());
        photo.setTrip(trip);
        String awsKey = s3PhotoService.uploadPhotoToS3Bucket(multipartFile, authentication.getUserId());
        photo.setAwsKey(awsKey);
        photoRepository.save(photo);
        return photoMapper.dataToDto(photo);
    }

    public void deletePhotoById(Long id, JwtAuthentication authentication) {
        Photo photo = findById(id);
        validateUserIsPhotoOwner(photo, authentication);
        s3PhotoService.deletePhotoByKey(photo.getAwsKey());
        photoRepository.delete(photo);
    }

    public PhotoDto updatePhoto(
            MultipartFile multipartFile,
            PhotoUpdateRequest photoUpdateRequest,
            JwtAuthentication authentication) {
        Photo photo = findById(photoUpdateRequest.photoId());
        validateUserIsPhotoOwner(photo, authentication);
        boolean isTitleUpdated = isPhotoTitleUpdated(photo, photoUpdateRequest);
        boolean isAwsKeyUpdated = isPhotoAwsKeyUpdated(photo, multipartFile, authentication.getUserId());
        if (isTitleUpdated || isAwsKeyUpdated) {
            photo = photoRepository.save(photo);
        }
        return photoMapper.dataToDto(photo);
    }

    private Photo findById(Long id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new DbObjectConflictException(PHOTO_NOT_FOUND));
    }

    @SneakyThrows
    private void validateUserIsPhotoOwner(Photo photo, JwtAuthentication authentication) {
        if (!authentication.isAdmin()
                && !photo.getTrip().getUser().getId().equals(authentication.getUserId())) {
            throw new AccessDeniedException(DONT_HAVE_PERMISSION_FOR_THAT);
        }
    }

    private boolean isPhotoTitleUpdated(Photo photo, PhotoUpdateRequest photoUpdateRequest) {
        boolean isPhotoUpdated = false;
        if (StringUtils.isNotEmpty(photoUpdateRequest.titleUpdate())
                && !photoUpdateRequest.titleUpdate().equals(photo.getTitle())) {
            photo.setTitle(photoUpdateRequest.titleUpdate());
            isPhotoUpdated = true;
        }
        return isPhotoUpdated;
    }

    private boolean isPhotoAwsKeyUpdated(Photo photo, MultipartFile multipartFile, Long userId) {
        boolean isPhotoUpdated = false;
        if (Objects.nonNull(photoMapper)) {
            s3PhotoService.deletePhotoByKey(photo.getAwsKey());
            String awsKey = s3PhotoService.uploadPhotoToS3Bucket(multipartFile, userId);
            photo.setAwsKey(awsKey);
            isPhotoUpdated = true;
        }
        return isPhotoUpdated;
    }
}