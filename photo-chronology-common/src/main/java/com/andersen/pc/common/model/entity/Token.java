package com.andersen.pc.common.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Setter
@RedisHash(value = "Token")
public class Token {

    @Id
    private Long id;
    @TimeToLive
    private Long ttl;
    @Indexed
    private String token;
    @Indexed
    private String userId;
    private String tokenType;
    private Long expiresIn;
    private Boolean isActive = true;
}
