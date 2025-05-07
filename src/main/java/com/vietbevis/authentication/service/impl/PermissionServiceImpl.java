package com.vietbevis.authentication.service.impl;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.vietbevis.authentication.dto.request.UpdatePermissionRequest;
import com.vietbevis.authentication.entity.PermissionEntity;
import com.vietbevis.authentication.exception.AlreadyExistsException;
import com.vietbevis.authentication.exception.NotFoundException;
import com.vietbevis.authentication.repository.PermissionRepository;
import com.vietbevis.authentication.service.PermissionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public PermissionEntity getPermissionById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền với id: " + id));
    }

    @Override
    public PermissionEntity updatePermission(Long id, UpdatePermissionRequest request) {
        PermissionEntity permission = getPermissionById(id);

        try {
            permission.setName(request.getName());
            permission.setDescription(request.getDescription());
            return permissionRepository.save(permission);
        } catch (Exception e) {
            log.error("Error updating permission: {}", e.getMessage());
            throw new AlreadyExistsException("Quyền đã tồn tại với tên: " + request.getName());
        }
    }

    @Override
    public Set<PermissionEntity> getPermissionsByRoleId(Long roleId) {
        return permissionRepository.findByRoleId(roleId);
    }

    @Override
    public Map<String, Set<PermissionEntity>> getAllPermissionGroupByResource() {
        return permissionRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        PermissionEntity::getResource,
                        Collectors.toSet()));
    }

    @Override
    public Set<PermissionEntity> getPermissionsByIds(Set<Long> permissionIds) {
        return permissionRepository.findAllById(permissionIds);
    }
}
