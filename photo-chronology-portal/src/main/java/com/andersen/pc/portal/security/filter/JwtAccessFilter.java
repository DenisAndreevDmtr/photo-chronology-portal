package com.andersen.pc.portal.security.filter;

import com.andersen.pc.portal.security.jwt.AccessJwtProvider;
import com.andersen.pc.portal.security.factory.JwtAuthenticationFactory;
import com.andersen.pc.portal.service.TokenService;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessFilter extends JwtFilter {

    private final AccessJwtProvider accessJwtProvider;

    @Autowired
    public JwtAccessFilter(
            AccessJwtProvider accessJwtProvider,
            JwtAuthenticationFactory jwtAuthenticationFactory,
            TokenService tokenService) {
        super(tokenService, jwtAuthenticationFactory);
        this.accessJwtProvider = accessJwtProvider;
    }

    @Override
    protected boolean isValidToken(@NonNull String token) {
        return accessJwtProvider.validateToken(token);
    }

    @Override
    protected Claims getTokenClaims(@NonNull String jwtToken) {
        return accessJwtProvider.getTokenClaims(jwtToken);
    }
}
