package com.andersen.pc.portal.security.jwt;

import com.andersen.pc.common.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.NonNull;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public abstract class JwtProvider {

    public abstract String getToken(@NonNull User user, @NonNull Instant validityTime);

    public abstract boolean validateToken(@NonNull String token) throws JwtException;

    public abstract Claims getTokenClaims(@NonNull String token);

    public Instant getValidityTokenTime(Integer minutes) {
        final LocalDateTime now = LocalDateTime.now();
        return now.plusMinutes(minutes)
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }

    public boolean validateToken(@NonNull String token, @NonNull SecretKey secret) throws JwtException {
        getJwsClaims(token, secret);
        return true;
    }

    public Claims getClaims(@NonNull String token, @NonNull SecretKey secret) {
        return getJwsClaims(token, secret).getBody();
    }

    private Jws<Claims> getJwsClaims(String token, SecretKey secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token);
    }
}
