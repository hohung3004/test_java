package com.project.salebe.dto.admin.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.salebe.entity.RoleEntity;
import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Data
public class AdminResponse {
    @JsonProperty("id")
    private String id;

    @JsonProperty("fullname")
    private String fullName;
    private String code;
    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("address")
    private String address;
    //    @JsonProperty("email")
    private String email;
    @JsonProperty("profile_image")
    private List<String> profileImage;

    @JsonProperty("is_active")
    private boolean active;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("facebook_account_id")
    private int facebookAccountId;

    @JsonProperty("google_account_id")
    private int googleAccountId;
    @JsonProperty("position_id")
    private String positionId;

    @JsonProperty("schedule_id")
    private String scheduleId;

    @JsonProperty("time_in")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime timeIn;
    @JsonProperty("role")
    private RoleEntity role;
    @JsonProperty("department_id")
    private String departmentId;
    @JsonProperty("is_main")
    private String isMain;
}
