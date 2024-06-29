package com.project.javatestfresher.dto.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @JsonProperty("user_name")
    private String userName;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
