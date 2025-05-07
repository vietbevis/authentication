package com.vietbevis.authentication.entity.base;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditEntity extends AbstractEntity {

    @CreatedBy
    @Column(name = "created_by_user_id", updatable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(name = "updated_by_user_id")
    private Long updatedBy;
}
