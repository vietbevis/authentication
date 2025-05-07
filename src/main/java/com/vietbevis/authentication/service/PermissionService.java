package com.vietbevis.authentication.service;

import java.util.Map;
import java.util.Set;

import com.vietbevis.authentication.dto.request.UpdatePermissionRequest;
import com.vietbevis.authentication.entity.PermissionEntity;

public interface PermissionService {

    PermissionEntity getPermissionById(Long id);

    Set<PermissionEntity> getPermissionsByRoleId(Long roleId);

    PermissionEntity updatePermission(Long id, UpdatePermissionRequest request);

    Map<String, Set<PermissionEntity>> getAllPermissionGroupByResource();

    Set<PermissionEntity> getPermissionsByIds(Set<Long> permissionIds);
}
