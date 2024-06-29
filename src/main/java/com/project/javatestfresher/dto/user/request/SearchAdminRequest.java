package com.project.javatestfresher.dto.user.request;

import com.project.javatestfresher.dto.BaseFilterRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.yaml.snakeyaml.scanner.Constant;

@Data
@EqualsAndHashCode(callSuper = false)
public class SearchAdminRequest extends BaseFilterRequest {
//    private String role = Constant.ROLE.ROLE_ADMIN;
}
