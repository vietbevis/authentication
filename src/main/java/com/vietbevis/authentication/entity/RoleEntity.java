package com.vietbevis.authentication.entity;

import com.vietbevis.authentication.entity.base.AuditEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class RoleEntity extends AuditEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @NaturalId(mutable = true)
  @Column(unique = true, nullable = false, length = 50)
  private String name;

  @Column()
  private String description;

  @Builder.Default
  @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "role_permissions",
      joinColumns = @JoinColumn(name = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "permission_id")
  )
  private Set<PermissionEntity> permissions = new HashSet<>();

  @Builder.Default
  @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
  private Set<UserEntity> users = new HashSet<>();
}
