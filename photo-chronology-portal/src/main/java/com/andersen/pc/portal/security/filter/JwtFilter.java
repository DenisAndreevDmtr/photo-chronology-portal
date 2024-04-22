package com.andersen.pc.portal.security.filter;

import com.andersen.pc.common.model.entity.Token;
import com.andersen.pc.portal.security.jwt.JwtAuthentication;
import com.andersen.pc.portal.security.factory.JwtAuthenticationFactory;
import com.andersen.pc.portal.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static com.andersen.pc.common.constant.Constant.Service.Token.AUTHORIZATION;
import static com.andersen.pc.common.constant.Constant.Service.Token.TOKEN_HEADER;

@RequiredArgsConstructor
public abstract class JwtFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final JwtAuthenticationFactory jwtAuthenticationFactory;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException, JwtException {
        final String jwtToken = getTokenFromRequest(request);
        JwtAuthentication currentAuthentication =
                (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(currentAuthentication) && Objects.nonNull(jwtToken)) {
            configureTokenData(jwtToken);
        }
        filterChain.doFilter(request, response);
    }

    protected abstract boolean isValidToken(@NonNull String token);

    protected abstract Claims getTokenClaims(@NonNull String jwtToken);

    private String getTokenFromRequest(HttpServletRequest request) {
        String headerAuthorization = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(headerAuthorization) && headerAuthorization.startsWith(TOKEN_HEADER)) {
            return headerAuthorization.substring(TOKEN_HEADER.length());
        }
        return null;
    }

    private void configureTokenData(String jwtToken) {
        Optional<Token> optionalToken = tokenService.findByToken(jwtToken);
        if (optionalToken.isPresent()) {
            Token dataToken = optionalToken.get();
            if (Boolean.TRUE.equals(dataToken.getIsActive()) && isValidToken(jwtToken)) {
                JwtAuthentication authentication = generateAuthentication(jwtToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }

    private JwtAuthentication generateAuthentication(String jwtToken) {
        Claims claims = getTokenClaims(jwtToken);
        return jwtAuthenticationFactory.create(claims);
    }
}
