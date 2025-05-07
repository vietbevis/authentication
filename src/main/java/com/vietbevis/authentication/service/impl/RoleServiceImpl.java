package com.vietbevis.authentication.service.impl;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.vietbevis.authentication.common.RoleBase;
import com.vietbevis.authentication.dto.request.CreateAndUpdateRoleRequest;
import com.vietbevis.authentication.entity.PermissionEntity;
import com.vietbevis.authentication.entity.RoleEntity;
import com.vietbevis.authentication.entity.UserEntity;
import com.vietbevis.authentication.exception.AlreadyExistsException;
import com.vietbevis.authentication.exception.ForbiddenException;
import com.vietbevis.authentication.exception.NotFoundException;
import com.vietbevis.authentication.repository.RoleRepository;
import com.vietbevis.authentication.repository.UserRepository;
import com.vietbevis.authentication.service.PermissionService;
import com.vietbevis.authentication.service.RoleService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionService permissionService;

    private Set<RoleEntity> baseRoles;

    @PostConstruct
    private void init() {
        baseRoles = roleRepository.findAll().stream()
                .filter(role -> RoleBase.isBaseRole(role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<RoleEntity> getBaseRoles() {
        return baseRoles;
    }

    @Override
    public RoleEntity getRoleUser() {
        Optional<RoleEntity> roleUser = baseRoles.stream()
                .filter(role -> RoleBase.USER.name().equals(role.getName()))
                .findFirst();

        if (roleUser.isEmpty()) {
            Optional<RoleEntity> role = roleRepository.findByName(RoleBase.USER.name());

            if (role.isEmpty()) {
                throw new NotFoundException("Không tìm thấy quyền USER");
            }

            baseRoles.add(role.get());
            return role.get();
        }

        return roleUser.get();
    }

    @Override
    public RoleEntity createRole(CreateAndUpdateRoleRequest request) {

        Set<PermissionEntity> permissions = permissionService.getPermissionsByIds(request.getPermissionIds());

        RoleEntity role = RoleEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .permissions(permissions)
                .build();

        try {
            return roleRepository.save(role);
        } catch (Exception e) {
            throw new AlreadyExistsException("Role đã tồn tại.");
        }

    }

    @Override
    public RoleEntity updateRole(Long id, CreateAndUpdateRoleRequest request) {

        if (baseRoles.stream().anyMatch(r -> r.getId().equals(id))) {
            throw new ForbiddenException("Không thể sửa đổi vai trò cơ bản.");
        }

        RoleEntity role = getRoleById(id);

        Set<PermissionEntity> permissions = permissionService.getPermissionsByIds(request.getPermissionIds());

        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setPermissions(permissions);

        try {
            return roleRepository.save(role);
        } catch (Exception e) {
            throw new AlreadyExistsException("Role đã tồn tại.");
        }
    }

    @Override
    public void deleteRole(Long id) {
        RoleEntity role = getRoleById(id);

        if (RoleBase.isBaseRole(role.getName())) {
            throw new ForbiddenException("Không thể xóa vai trò cơ bản.");
        }

        roleRepository.deleteById(id);
    }

    @Override
    public RoleEntity getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role không tồn tại."));
    }

    @Override
    public Set<RoleEntity> getAllRoles() {
        return roleRepository.findAll().stream().collect(Collectors.toSet());
    }

    @Override
    public Set<RoleEntity> getRolesByUserId(Long userId) {
        return roleRepository.findByUsersId(userId).stream().collect(Collectors.toSet());
    }

    @Override
    public void addRolesToUser(Long userId, Set<Long> roleIds) {

        if (baseRoles.stream().anyMatch(role -> roleIds.contains(role.getId()))) {
            throw new ForbiddenException("Không thể thêm vai trò cơ bản cho người dùng.");
        }

        UserEntity user = userRepository.findWithRolesById(userId)
                .orElseThrow(() -> new NotFoundException("User không tồn tại."));

        Set<RoleEntity> roles = roleRepository.findByIdIn(roleIds);

        if (roleIds.size() != roles.size()) {
            throw new NotFoundException("Một số vai trò không tồn tại.");
        }

        user.getRoles().addAll(roles);

        userRepository.save(user);
    }

    @Override
    public void removeRolesFromUser(Long userId, Set<Long> roleIds) {

        if (baseRoles.stream().anyMatch(role -> roleIds.contains(role.getId()))) {
            throw new ForbiddenException("Không thể xóa vai trò cơ bản của người dùng.");
        }

        UserEntity user = userRepository.findWithRolesById(userId)
                .orElseThrow(() -> new NotFoundException("User không tồn tại."));

        Set<RoleEntity> rolesToRemove = roleRepository.findByIdIn(roleIds);

        if (roleIds.size() != rolesToRemove.size()) {
            throw new NotFoundException("Một số vai trò không tồn tại.");
        }

        // Tạo một Set chứa ID của các vai trò cần xóa để tìm kiếm hiệu quả hơn
        Set<Long> roleIdsToRemove = rolesToRemove.stream()
                .map(RoleEntity::getId)
                .collect(Collectors.toSet());

        // Lọc các vai trò cần giữ lại
        Set<RoleEntity> updatedRoles = user.getRoles().stream()
                .filter(role -> !roleIdsToRemove.contains(role.getId()))
                .collect(Collectors.toSet());

        // Cập nhật danh sách vai trò của user
        user.getRoles().clear();
        user.getRoles().addAll(updatedRoles);

        // Lưu thay đổi
        userRepository.save(user);
    }

    @Override
    public void addPermissionsToRole(Long roleId, Set<Long> permissionIds) {
        RoleEntity role = roleRepository.findWithPermissionsById(roleId)
                .orElseThrow(() -> new NotFoundException("Không tồn tại vai trò."));

        Set<PermissionEntity> permissions = permissionService.getPermissionsByIds(permissionIds);

        if (permissionIds.size() != permissions.size()) {
            throw new NotFoundException("Một số quyền không tồn tại.");
        }

        Set<Long> currentPermissionIds = role.getPermissions().stream()
                .map(PermissionEntity::getId)
                .collect(Collectors.toSet());

        Set<PermissionEntity> updatedPermissions = permissions.stream()
                .filter(permission -> !currentPermissionIds.contains(permission.getId()))
                .collect(Collectors.toSet());

        if (updatedPermissions.size() == 0) {
            throw new AlreadyExistsException("Vai trò đã có tất cả quyền.");
        }

        role.getPermissions().addAll(updatedPermissions);
        roleRepository.save(role);
    }

    @Override
    public void removePermissionsFromRole(Long roleId, Set<Long> permissionIds) {
        RoleEntity role = roleRepository.findWithPermissionsById(roleId)
                .orElseThrow(() -> new NotFoundException("Không tồn tại vai trò."));

        Set<PermissionEntity> permissions = permissionService.getPermissionsByIds(permissionIds);

        if (permissionIds.size() != permissions.size()) {
            throw new NotFoundException("Một số quyền không tồn tại.");
        }

        Set<Long> permissionIdsToRemove = permissions.stream()
                .map(PermissionEntity::getId)
                .collect(Collectors.toSet());

        Set<PermissionEntity> updatedPermissions = role.getPermissions().stream()
                .filter(permission -> !permissionIdsToRemove.contains(permission.getId()))
                .collect(Collectors.toSet());

        role.getPermissions().clear();
        role.getPermissions().addAll(updatedPermissions);

        roleRepository.save(role);
    }

}
