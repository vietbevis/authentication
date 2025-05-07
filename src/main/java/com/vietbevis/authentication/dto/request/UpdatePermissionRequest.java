package com.vietbevis.authentication.dto.request;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdatePermissionRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Tên quyền không được để trống")
    @Size(min = 5, max = 50, message = "Tên quyền phải có từ 5 đến 50 ký tự")
    private String name;

    @NotNull(message = "Mô tả quyền không được để trống")
    @Size(min = 5, max = 255, message = "Mô tả quyền phải có từ 5 đến 255 ký tự")
    private String description;
}
