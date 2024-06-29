package com.project.salebe.dto.admin.request;

import com.project.salebe.constant.Constant;
import com.project.salebe.dto.BaseFilterRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SearchAdminRequest extends BaseFilterRequest {
    private String role = Constant.ROLE.ROLE_ADMIN;
}
