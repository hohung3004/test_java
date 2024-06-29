package com.project.javatestfresher.controller;


import com.project.javatestfresher.dto.auth.ResponseObject;
import com.project.javatestfresher.dto.user.request.*;
import com.project.javatestfresher.dto.user.response.AdminResponse;
import com.project.javatestfresher.dto.user.response.ImportToolResponse;
import com.project.javatestfresher.dto.user.response.SearchUserResponse;
import com.project.javatestfresher.service.UserService;
import com.project.javatestfresher.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/admin")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    JsonUtil jsonUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/search")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public SearchUserResponse searchAdmin(@RequestBody SearchAdminRequest searchRequest) {
        log.info("User search admin: {}", jsonUtil.toJson(searchRequest));
        return userService.searchAdmin(searchRequest);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_DEVELOPER')")
    public AdminResponse getById(@PathVariable String id) {
        log.info("Xem chi tiet admin viu id : {}", id);
        return userService.getAdminById(id);
    }

        @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/register")
    public AdminResponse createAdmin(@Valid @RequestBody AdminRequest request) {
        log.info("Admin create user : {}", request.getUserName());
        return userService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public AdminResponse updateAdmin(@PathVariable String id, @Validated @RequestBody AndminUpdateRequest request) {
        log.info("chinh sua thong tin nhan vien voi id {} va thong tin {}", id, request.getUserName());
        return userService.update(id, request);
    }

    @PutMapping("/reset-password/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_DEVELOPER')")
    public ResponseEntity<ResponseObject> resetPassword(@PathVariable String id, @RequestBody ResetPassword resetPassword) {
        log.info("Thay doi mat khau : {}", id);
        userService.resetPassword(id, resetPassword);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Reset password successfully")
                .data(resetPassword.getPassword())
                .status(HttpStatus.OK)
                .build());
    }
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/import")
    public ImportToolResponse importTools(@RequestBody ImportUserRequest importToolRequest){
        log.info(" import user: {}", jsonUtil.toJson(importToolRequest));
        return userService.importTools(importToolRequest);
    }
}