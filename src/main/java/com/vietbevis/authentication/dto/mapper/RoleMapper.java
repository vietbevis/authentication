package com.vietbevis.authentication.dto.mapper;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vietbevis.authentication.dto.request.CreateAndUpdateRoleRequest;
import com.vietbevis.authentication.dto.response.RoleResponse;
import com.vietbevis.authentication.entity.RoleEntity;

@Mapper(componentModel = "spring", uses = PermissionMapper.class)
public interface RoleMapper {

    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    RoleEntity toEntity(CreateAndUpdateRoleRequest request);

    RoleResponse toResponse(RoleEntity roleEntity);

    Set<RoleResponse> toResponse(Set<RoleEntity> roleEntities);
}
