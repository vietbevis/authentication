package com.vietbevis.authentication.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.vietbevis.authentication.common.RoleBase;
import com.vietbevis.authentication.dto.request.CreateAndUpdateRoleRequest;
import com.vietbevis.authentication.entity.PermissionEntity;
import com.vietbevis.authentication.entity.RoleEntity;
import com.vietbevis.authentication.exception.AlreadyExistsException;
import com.vietbevis.authentication.exception.ForbiddenException;
import com.vietbevis.authentication.exception.NotFoundException;
import com.vietbevis.authentication.repository.RoleRepository;
import com.vietbevis.authentication.service.PermissionService;
import com.vietbevis.authentication.service.RoleService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionService permissionService;

    private Set<RoleEntity> baseRoles;

    @PostConstruct
    public void init() {
        baseRoles = roleRepository.findAll().stream()
                .filter(role -> RoleBase.isBaseRole(role.getName()))
                .collect(Collectors.toSet());
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
        if (baseRoles.stream().anyMatch(r -> r.getId().equals(id))) {
            throw new ForbiddenException("Không thể sửa đổi vai trò cơ bản.");
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

}
