package com.vietbevis.authentication.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietbevis.authentication.common.HttpMethod;
import com.vietbevis.authentication.entity.PermissionEntity;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {

    Optional<PermissionEntity> findByApiPathAndMethod(String apiPath, HttpMethod method);

    @Query("SELECT p FROM PermissionEntity p JOIN p.roles r WHERE r.id = :roleId")
    Set<PermissionEntity> findByRoleId(@Param("roleId") Long roleId);

    Set<PermissionEntity> findAllById(Set<Long> permissionIds);
}
