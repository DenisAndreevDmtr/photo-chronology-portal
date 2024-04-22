package com.andersen.pc.portal.security.factory;

import com.andersen.pc.common.model.entity.User;
import com.andersen.pc.portal.security.jwt.AccessJwtProvider;
import com.andersen.pc.common.model.entity.Token;
import com.andersen.pc.portal.security.util.ValueHashing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.andersen.pc.common.constant.Constant.Service.Token.TOKEN_TYPE;

@Service
@RequiredArgsConstructor
public class TokenFactory {

    private final AccessJwtProvider accessJwtProvider;
    private static final Integer ACCESS_TOKEN_VALIDITY_IN_MINUTES = 20;
    private static final Long ACCESS_TOKEN_TTL_IN_SECONDS = 1200L;

    public Token createAccessToken(User user) {
        final Instant validityTime = accessJwtProvider.getValidityTokenTime(ACCESS_TOKEN_VALIDITY_IN_MINUTES);
        final String accessToken = accessJwtProvider.getToken(user, validityTime);
        String hashUserId = ValueHashing.getHash(user.getId().toString());
        return getAccessToken(accessToken, hashUserId, validityTime);
    }

    public Token getAccessToken(String accessToken, String hashUserId, Instant validityTime) {
        Token token = new Token();
        token.setTtl(ACCESS_TOKEN_TTL_IN_SECONDS);
        token.setToken(accessToken);
        token.setTokenType(TOKEN_TYPE);
        token.setUserId(hashUserId);
        token.setExpiresIn(validityTime.getEpochSecond());
        return token;
    }
}
