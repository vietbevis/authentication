package com.vietbevis.authentication.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietbevis.authentication.entity.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    @Query("SELECT r FROM RoleEntity r JOIN r.users u WHERE u.id = :userId")
    Set<RoleEntity> findByUsersId(@Param("userId") Long userId);

    Set<RoleEntity> findByNameIn(Set<String> names);

    Set<RoleEntity> findByIdIn(Set<Long> ids);

    Optional<RoleEntity> findWithPermissionsById(@Param("id") Long id);

    Optional<RoleEntity> findByName(String name);
}
