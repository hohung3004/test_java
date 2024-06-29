package com.project.salebe.dto.admin.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResetPassword {
    private String password;

    @JsonProperty("retype_password")
    private String retypePassword;
}