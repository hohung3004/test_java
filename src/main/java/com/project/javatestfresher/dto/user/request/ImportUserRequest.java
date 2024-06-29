package com.project.javatestfresher.dto.user.request;

import lombok.Data;

import java.util.List;

@Data
public class ImportToolRequest {
    List<AdminRequest> listTool;
}
