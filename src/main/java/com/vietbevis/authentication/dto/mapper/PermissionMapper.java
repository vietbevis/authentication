package com.vietbevis.authentication.dto.mapper;

import java.util.Set;

import org.mapstruct.Mapper;

import com.vietbevis.authentication.dto.response.PermissionResponse;
import com.vietbevis.authentication.entity.PermissionEntity;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionResponse toResponse(PermissionEntity permissionEntity);

    Set<PermissionResponse> toResponse(Set<PermissionEntity> permissionEntities);
}
