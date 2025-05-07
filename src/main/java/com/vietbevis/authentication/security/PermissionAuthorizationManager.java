package com.vietbevis.authentication.security;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PermissionAuthorizationManager implements
    AuthorizationManager<RequestAuthorizationContext> {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication,
            RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();
        Object principal = authentication.get().getPrincipal();

        if (principal.equals("anonymousUser")) {
            return new AuthorizationDecision(false);
        }

        UserPrincipal userDetails = (UserPrincipal) principal;

        boolean hasPermission = userDetails.getPermissions().stream()
                .anyMatch(p -> pathMatcher.match(p.getApiPath(),
                        request.getRequestURI().substring(contextPath.length())) &&
                        p.getMethod().toString().equalsIgnoreCase(request.getMethod()));

        return new AuthorizationDecision(hasPermission);
    }
}
