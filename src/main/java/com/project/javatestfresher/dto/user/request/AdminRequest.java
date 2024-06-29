package com.project.salebe.dto.admin.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
@Data
public class AdminRequest {
    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber = "";

    @JsonProperty("email")
    private String email = "";

    private String address = "";

    @JsonProperty("date_of_birth")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date dateOfBirth;

    @JsonProperty("facebook_account_id")
    private int facebookAccountId;

    @JsonProperty("google_account_id")
    private int googleAccountId;
    @JsonProperty("position_id")
    private String positionId;

    @JsonProperty("schedule_id")
    private String scheduleId;
    @NotNull(message = "Role ID is required")
    @JsonProperty("role_id")
    //role admin not permitted
    private String roleId;
}
