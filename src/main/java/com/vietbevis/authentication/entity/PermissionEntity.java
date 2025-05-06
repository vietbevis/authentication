package com.vietbevis.authentication.entity;

import com.vietbevis.authentication.common.HttpMethod;
import com.vietbevis.authentication.entity.base.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "permissions",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"api_path", "method"}, name = "uk_api_path_method"),
        @UniqueConstraint(columnNames = {"name"}, name = "uk_name")
    }
)
public class PermissionEntity extends AbstractEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Column(nullable = false, length = 100)
  private String name;

  @Column()
  private String description;

  @Column(nullable = false, name = "api_path", length = 200)
  private String apiPath;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private HttpMethod method;

  @Column(nullable = false, length = 100)
  private String resource;

  @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
  @Builder.Default
  private Set<RoleEntity> roles = new HashSet<>();
}
