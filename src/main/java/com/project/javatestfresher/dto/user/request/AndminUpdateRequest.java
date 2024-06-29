package com.project.javatestfresher.dto.user.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AndminUpdateRequest {
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("created_at")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate createdAt;
    @JsonProperty("valid_until")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate validUntil;

    @JsonProperty("is_valid")
    private boolean isValid;
    @NotNull(message = "Role ID is required")
    @JsonProperty("role_id")
    //role admin not permitted
    private String roleId;
}
