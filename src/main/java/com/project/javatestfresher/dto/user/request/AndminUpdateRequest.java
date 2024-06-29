package com.project.salebe.dto.admin.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AndminUpdateRequest {
    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber = "";

    @JsonProperty("email")
    private String email = "";

    private String address = "";

    @JsonProperty("profile_image")
    private List<MultipartFile> profileImage;
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
    @NotNull(message = "Role ID is required")
    @JsonProperty("role_id")
    //role admin not permitted
    private String roleId;
}
