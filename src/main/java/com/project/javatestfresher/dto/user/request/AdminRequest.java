package com.project.javatestfresher.dto.user.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
@Data
public class AdminRequest {
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("created_at")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate createdAt;
    @JsonProperty("valid_until")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date validUntil;

    private String password;
    @JsonProperty("retype_password")
    private String retypePassword;
    @JsonProperty("is_valid")
    private boolean isValid;
    @NotNull(message = "Role ID is required")
    @JsonProperty("role_id")
    //role admin not permitted
    private String roleId;
}
