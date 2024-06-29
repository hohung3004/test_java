package com.project.javatestfresher.dto.user.request;

import lombok.Data;

import java.util.List;

@Data
public class ImportUserRequest {
    List<AdminRequest> list;
}
