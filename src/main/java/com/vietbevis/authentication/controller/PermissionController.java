package com.vietbevis.authentication.controller;

import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietbevis.authentication.dto.mapper.PermissionMapper;
import com.vietbevis.authentication.dto.request.UpdatePermissionRequest;
import com.vietbevis.authentication.dto.response.ApiResponse;
import com.vietbevis.authentication.dto.response.PermissionResponse;
import com.vietbevis.authentication.dto.response.ResponseUtil;
import com.vietbevis.authentication.entity.PermissionEntity;
import com.vietbevis.authentication.service.PermissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Permissions", description = "Permissions API")
@RestController
@RequestMapping("/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;
    private final PermissionMapper permissionMapper;

    @Operation(summary = "Lấy quyền theo ID", description = "Lấy quyền theo ID")
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> getPermissionById(
            @Valid @PathVariable("id") Long id) {
        PermissionEntity permission = permissionService.getPermissionById(id);
        return ResponseUtil.success(permissionMapper.toResponse(permission));
    }

    @Operation(summary = "Cập nhật quyền", description = "Cập nhật quyền")
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> updatePermission(
            @Valid @PathVariable("id") Long id,
            @Valid @RequestBody UpdatePermissionRequest request) {
        PermissionEntity updatedPermission = permissionService.updatePermission(id, request);
        return ResponseUtil.success("Cập nhật quyền thành công.",
                permissionMapper.toResponse(updatedPermission));
    }

    @Operation(summary = "Lấy quyền theo vai trò ID", description = "Lấy quyền theo vai trò ID")
    @GetMapping("role/{id}")
    public ResponseEntity<ApiResponse<Set<PermissionResponse>>> getPermissionsByRoleId(
            @Valid @PathVariable("id") Long id) {
        return ResponseUtil.success(permissionMapper.toResponse(permissionService.getPermissionsByRoleId(id)));
    }

    @Operation(summary = "Lấy quyền theo tài nguyên", description = "Lấy quyền theo tài nguyên")
    @GetMapping("group-by-resource")
    public ResponseEntity<ApiResponse<Map<String, Set<PermissionResponse>>>> getAllPermissionGroupByResource() {
        Map<String, Set<PermissionEntity>> permissionsByResource = permissionService.getAllPermissionGroupByResource();
        Map<String, Set<PermissionResponse>> response = permissionsByResource.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> permissionMapper.toResponse(entry.getValue())));
        return ResponseUtil.success(response);
    }
}
