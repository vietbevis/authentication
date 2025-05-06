package com.vietbevis.authentication.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietbevis.authentication.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

    ApiResponse<Object> errorResponse = ApiResponse.error(
        HttpStatus.FORBIDDEN.value(),
        "Bạn không có quyền truy cập vào tài nguyên này.");

    OutputStream responseStream = response.getOutputStream();
    objectMapper.findAndRegisterModules();
    objectMapper.writeValue(responseStream, errorResponse);
    responseStream.flush();
  }
}
