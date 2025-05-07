package com.vietbevis.authentication.service;

import java.util.Set;

import com.vietbevis.authentication.dto.request.CreateAndUpdateRoleRequest;
import com.vietbevis.authentication.entity.RoleEntity;

public interface RoleService {

    RoleEntity createRole(CreateAndUpdateRoleRequest request);

    RoleEntity updateRole(Long id, CreateAndUpdateRoleRequest request);

    void deleteRole(Long id);

    RoleEntity getRoleById(Long id);

    Set<RoleEntity> getAllRoles();

    Set<RoleEntity> getRolesByUserId(Long userId);

    Set<RoleEntity> getBaseRoles();

    RoleEntity getRoleUser();

    void addRolesToUser(Long userId, Set<Long> roleIds);

    void removeRolesFromUser(Long userId, Set<Long> roleIds);

    void addPermissionsToRole(Long roleId, Set<Long> permissionIds);

    void removePermissionsFromRole(Long roleId, Set<Long> permissionIds);

}
