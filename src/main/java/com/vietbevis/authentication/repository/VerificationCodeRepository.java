package com.vietbevis.authentication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.vietbevis.authentication.common.VerificationCodeType;
import com.vietbevis.authentication.entity.VerificationCodeEntity;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCodeEntity, Long> {
    Optional<VerificationCodeEntity> findByEmailAndCodeAndType(
            String email,
            String code,
            VerificationCodeType type);

    @Modifying
    @Transactional
    @Query("DELETE FROM VerificationCodeEntity v WHERE v.expiresAt < CURRENT_TIMESTAMP")
    void deleteByExpiresAtBeforeCurrentTimestamp();

    void deleteByEmailAndCodeAndType(
            String email,
            String code,
            VerificationCodeType type);
}
