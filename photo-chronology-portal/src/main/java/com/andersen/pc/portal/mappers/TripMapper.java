package com.andersen.pc.portal.mappers;

import com.andersen.pc.common.model.dto.request.TripCreationRequest;
import com.andersen.pc.common.model.dto.response.TripDto;
import com.andersen.pc.common.model.entity.Trip;
import com.andersen.pc.common.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PhotoMapper.class})
public interface TripMapper {

    @Mapping(source = "user", target = "user")
    Trip dtoToData(TripCreationRequest tripCreationRequest, User user);

    @Mapping(source = "user.id", target = "userId")
    TripDto dataToDto(Trip trip);

    List<TripDto> dataListToDtoList(List<Trip> trip);
}
