package com.vietbevis.authentication.dto.request;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAndUpdateRoleRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Tên vai trò không được để trống")
    @NotBlank(message = "Tên vai trò không được để trống")
    @Size(min = 5, max = 50, message = "Tên vai trò phải có từ 5 đến 50 ký tự")
    private String name;

    @NotNull(message = "Mô tả vai trò không được để trống")
    @NotBlank(message = "Mô tả vai trò không được để trống")
    @Size(min = 5, max = 255, message = "Mô tả vai trò phải có từ 5 đến 255 ký tự")
    private String description;

    @NotNull(message = "Quyền không được để trống")
    @NotEmpty(message = "Quyền không được để trống")
    private Set<Long> permissionIds;
}
