package com.andersen.pc.portal.security.jwt;

import com.andersen.pc.common.model.UserRole;
import com.andersen.pc.common.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import static com.andersen.pc.common.constant.Constant.Service.Token.TOKEN_CLAIM_EMAIL;
import static com.andersen.pc.common.constant.Constant.Service.Token.TOKEN_CLAIM_ROLES;
import static com.andersen.pc.common.constant.Constant.Service.Token.TOKEN_CLAIM_USERNAME;

@Component
public class AccessJwtProvider extends JwtProvider {

    private final SecretKey jwtAccessSecret;

    public AccessJwtProvider(@Value("${jwt.secret.access}") String jwtAccessSecret) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
    }

    @Override
    public String getToken(@NonNull User user, @NonNull Instant validityTime) {
        Set<UserRole> authorityRoles = user.getUserRoles().stream()
                .map(x -> x.getRole().getRoleName())
                .collect(Collectors.toSet());
        return Jwts.builder()
                .setExpiration(Date.from(validityTime))
                .claim(TOKEN_CLAIM_USERNAME, user.getName())
                .claim(TOKEN_CLAIM_EMAIL, user.getEmail())
                .claim(TOKEN_CLAIM_ROLES, authorityRoles)
                .signWith(jwtAccessSecret)
                .compact();
    }

    @Override
    public boolean validateToken(@NonNull String token) throws JwtException {
        return validateToken(token, jwtAccessSecret);
    }

    @Override
    public Claims getTokenClaims(@NonNull String token) {
        return getClaims(token, jwtAccessSecret);
    }
}
