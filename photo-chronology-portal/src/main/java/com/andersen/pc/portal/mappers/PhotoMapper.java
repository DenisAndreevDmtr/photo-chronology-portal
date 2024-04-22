package com.andersen.pc.portal.mappers;

import com.andersen.pc.common.model.dto.response.PhotoDto;
import com.andersen.pc.common.model.entity.Photo;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface PhotoMapper {

    PhotoDto dataToDto(Photo photo);
    Set<PhotoDto> dataSetToDtoSet(Set<Photo> photos);
}
