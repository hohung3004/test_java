package com.project.javatestfresher.dto.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResetPassword {
    private String password;

    @JsonProperty("retype_password")
    private String retypePassword;
}