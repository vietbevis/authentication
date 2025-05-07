package com.vietbevis.authentication.common;

import java.util.List;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    OPTIONS,
    HEAD;
 
    public static HttpMethod fromString(String method) {
        return HttpMethod.valueOf(method.toUpperCase());
    }

    public static List<String> getAllMethods() {
        return List.of(
            GET.name(),
            POST.name(),
            PUT.name(),
            DELETE.name(),
            PATCH.name(),
            OPTIONS.name(),
            HEAD.name());
    }
}
