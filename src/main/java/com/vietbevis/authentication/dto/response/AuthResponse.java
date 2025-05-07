package com.vietbevis.authentication.dto.response;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String accessToken;

    private String refreshToken;

}
