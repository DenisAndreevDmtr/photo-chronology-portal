package com.andersen.pc.portal.service;

import com.andersen.pc.common.model.dto.response.TokenDto;
import com.andersen.pc.portal.mappers.TokenMapper;
import com.andersen.pc.common.model.entity.Token;
import com.andersen.pc.portal.repository.TokenRepository;
import com.andersen.pc.portal.security.util.ValueHashing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final TokenMapper tokenMapper;

    public Optional<Token> findByToken(String jwtToken) {
        return tokenRepository.findTokenByToken(jwtToken);
    }

    public TokenDto saveAndGetApi(Token token, Long userId) {
        tokenRepository.save(token);
        return tokenMapper.dataToApi(token, userId);
    }

    public void deleteTokensByUserId(Long userId) {
        String hashUserId = ValueHashing.getHash(userId.toString());
        Set<Token> tokens = tokenRepository.findTokensByUserId(hashUserId);
        tokenRepository.deleteAll(tokens);
    }
}
