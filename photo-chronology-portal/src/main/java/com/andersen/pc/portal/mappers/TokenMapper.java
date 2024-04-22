package com.andersen.pc.portal.mappers;

import com.andersen.pc.common.model.dto.response.TokenDto;
import com.andersen.pc.common.model.entity.Token;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TokenMapper {

    @Mapping(source = "tokenUserId", target = "userId")
    TokenDto dataToApi(Token token, Long tokenUserId);
}
