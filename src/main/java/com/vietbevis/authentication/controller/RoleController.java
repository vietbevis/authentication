package com.vietbevis.authentication.controller;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietbevis.authentication.dto.mapper.RoleMapper;
import com.vietbevis.authentication.dto.request.CreateAndUpdateRoleRequest;
import com.vietbevis.authentication.dto.response.ApiResponse;
import com.vietbevis.authentication.dto.response.ResponseUtil;
import com.vietbevis.authentication.dto.response.RoleResponse;
import com.vietbevis.authentication.entity.RoleEntity;
import com.vietbevis.authentication.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Roles", description = "Roles API")
@RestController
@RequestMapping("/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @Operation(summary = "Lấy tất cả vai trò", description = "Lấy tất cả vai trò")
    @GetMapping
    public ResponseEntity<ApiResponse<Set<RoleResponse>>> getAllRoles() {
        Set<RoleEntity> roles = roleService.getAllRoles();
        return ResponseUtil.success(roleMapper.toResponse(roles));
    }

    @Operation(summary = "Lấy vai trò theo ID", description = "Lấy vai trò theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@Valid @PathVariable Long id) {
        RoleEntity role = roleService.getRoleById(id);
        return ResponseUtil.success(roleMapper.toResponse(role));
    }

    @Operation(summary = "Lấy vai trò theo ID người dùng", description = "Lấy vai trò theo ID người dùng")
    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse<Set<RoleResponse>>> getRolesByUserId(@Valid @PathVariable Long id) {
        Set<RoleEntity> roles = roleService.getRolesByUserId(id);
        return ResponseUtil.success(roleMapper.toResponse(roles));
    }

    @Operation(summary = "Tạo vai trò", description = "Tạo vai trò")
    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(
            @Valid @RequestBody CreateAndUpdateRoleRequest request) {
        RoleEntity role = roleService.createRole(request);
        return ResponseUtil.success(roleMapper.toResponse(role));
    }

    @Operation(summary = "Cập nhật vai trò", description = "Cập nhật vai trò")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(
            @Valid @PathVariable Long id,
            @Valid @RequestBody CreateAndUpdateRoleRequest request) {
        RoleEntity role = roleService.updateRole(id, request);
        return ResponseUtil.success(roleMapper.toResponse(role));
    }

    @Operation(summary = "Xóa vai trò", description = "Xóa vai trò")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteRole(@Valid @PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseUtil.success("Xóa vai trò thành công");
    }

    @Operation(summary = "Thêm vai trò cho người dùng", description = "Thêm vai trò cho người dùng")
    @PostMapping("/user/{id}")
    public ResponseEntity<ApiResponse<String>> addRolesToUser(@Valid @PathVariable Long id,
            @Valid @RequestBody Set<Long> roleIds) {
        roleService.addRolesToUser(id, roleIds);
        return ResponseUtil.success("Thêm vai trò thành công");
    }

    @Operation(summary = "Xóa vai trò cho người dùng", description = "Xóa vai trò cho người dùng")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<ApiResponse<String>> removeRolesFromUser(@Valid @PathVariable Long id,
            @Valid @RequestBody Set<Long> roleIds) {
        roleService.removeRolesFromUser(id, roleIds);
        return ResponseUtil.success("Xóa vai trò thành công");
    }

    @Operation(summary = "Thêm quyền cho vai trò", description = "Thêm quyền cho vai trò")
    @PostMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<String>> addPermissionsToRole(@Valid @PathVariable Long id,
            @Valid @RequestBody Set<Long> permissionIds) {
        roleService.addPermissionsToRole(id, permissionIds);
        return ResponseUtil.success("Thêm quyền thành công");
    }

    @Operation(summary = "Xóa quyền cho vai trò", description = "Xóa quyền cho vai trò")
    @DeleteMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<String>> removePermissionsFromRole(@Valid @PathVariable Long id,
            @Valid @RequestBody Set<Long> permissionIds) {
        roleService.removePermissionsFromRole(id, permissionIds);
        return ResponseUtil.success("Xóa quyền thành công");
    }
}
