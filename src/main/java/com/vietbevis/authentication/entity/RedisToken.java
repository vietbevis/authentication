package com.vietbevis.authentication.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("access_tokens")
public class RedisToken implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String token;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long expiration;
}
