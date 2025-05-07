package com.vietbevis.authentication.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.vietbevis.authentication.common.HttpMethod;
import com.vietbevis.authentication.common.RoleBase;
import com.vietbevis.authentication.entity.PermissionEntity;
import com.vietbevis.authentication.entity.RoleEntity;
import com.vietbevis.authentication.repository.PermissionRepository;
import com.vietbevis.authentication.repository.RoleRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RequestMappingHandlerMapping handlerMapping;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EndpointInfo {

        private String path;
        private HttpMethod method;
        private String handlerMethod;
    }

    @Autowired
    public DataInitializer(
            @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping,
            PermissionRepository permissionRepository,
            RoleRepository roleRepository) {
        this.handlerMapping = handlerMapping;
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<EndpointInfo> endpoints = scanEndpoints();
        List<PermissionEntity> createdPermissions = scanAndCreatePermissions(endpoints);

        syncRoles(createdPermissions);

        System.out.println("Created permissions: " + createdPermissions);
    }

    private void syncRoles(List<PermissionEntity> createdPermissions) {
        Set<RoleEntity> roles = roleRepository.findByNameIn(Arrays.stream(RoleBase.values())
                .map(RoleBase::name)
                .collect(Collectors.toSet()));

        Set<RoleEntity> missingRoles = Arrays.stream(RoleBase.values())
                .filter(role -> !roles.stream().anyMatch(r -> r.getName().equals(role.name())))
                .map(role -> RoleEntity.builder()
                        .name(role.name())
                        .description(role.name())
                        .permissions(RoleBase.ADMIN.equals(role) ? createdPermissions.stream()
                                .collect(Collectors.toSet()) : new HashSet<>())
                        .build())
                .collect(Collectors.toSet());

        roleRepository.saveAll(missingRoles);
    }

    private List<PermissionEntity> scanAndCreatePermissions(List<EndpointInfo> endpoints) {
        List<PermissionEntity> createdPermissions = new ArrayList<>();

        for (EndpointInfo endpoint : endpoints) {
            String permissionName = generatePermissionName(endpoint.getMethod(),
                    endpoint.getPath());
            String resource = extractResourceFromPath(endpoint.getPath());

            Optional<PermissionEntity> existingPermission = permissionRepository.findByApiPathAndMethod(
                    endpoint.getPath(), endpoint.getMethod());

            if (existingPermission.isEmpty()) {
                PermissionEntity newPermission = PermissionEntity.builder()
                        .name(permissionName)
                        .description(endpoint.getPath())
                        .apiPath(endpoint.getPath())
                        .method(endpoint.getMethod())
                        .resource(resource)
                        .build();

                createdPermissions.add(permissionRepository.save(newPermission));
            } else {
                PermissionEntity existing = existingPermission.get();

                existing.setApiPath(endpoint.getPath());
                existing.setMethod(endpoint.getMethod());
                existing.setResource(resource);

                createdPermissions.add(permissionRepository.save(existing));
            }
        }

        return createdPermissions;
    }

    private String generatePermissionName(HttpMethod method, String path) {
        String resource = extractResourceFromPath(path);
        String action = method.name();

        return "[" + action + "_" + resource.toUpperCase() + "] " + path;
    }

    private String extractResourceFromPath(String path) {
        String[] pathParts = path.replaceAll("^/+", "").split("/");

        if (pathParts.length > 1) {
            return pathParts[1].toLowerCase();
        }

        return "general";
    }

    private List<EndpointInfo> scanEndpoints() {
        List<EndpointInfo> endpoints = new ArrayList<>();

        handlerMapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {
            if (shouldSkipEndpoint(handlerMethod)) {
                return;
            }

            assert requestMappingInfo.getPathPatternsCondition() != null;
            requestMappingInfo.getPathPatternsCondition().getPatterns().forEach(
                    pattern -> requestMappingInfo.getMethodsCondition().getMethods()
                            .forEach(method -> endpoints.add(EndpointInfo.builder()
                                    .path(pattern.getPatternString())
                                    .method(HttpMethod.fromString(method.name()))
                                    .handlerMethod(handlerMethod.toString())
                                    .build())));
        });

        return endpoints;
    }

    private boolean shouldSkipEndpoint(HandlerMethod handlerMethod) {
        String packageName = handlerMethod.getBeanType().getPackage().getName();
        return packageName.startsWith("org.springframework") || packageName.startsWith(
                "org.springdoc")
                || packageName.contains("actuator")
                || handlerMethod.getBeanType().getSimpleName().contains("Error");
    }
}
