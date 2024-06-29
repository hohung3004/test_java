package com.project.javatestfresher.dto.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ImportToolResponse {
    private String message;
    @JsonProperty("list_error")
    private List<ToolError> listError;
}