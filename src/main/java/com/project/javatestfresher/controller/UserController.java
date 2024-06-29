package com.project.salebe.controller;

import com.project.salebe.dto.admin.request.AdminRegisterRequest;
import com.project.salebe.dto.admin.request.AndminUpdateRequest;
import com.project.salebe.dto.admin.request.ResetPassword;
import com.project.salebe.dto.admin.request.SearchAdminRequest;
import com.project.salebe.dto.admin.response.AdminResponse;
import com.project.salebe.dto.admin.response.SearchAdminResponse;
import com.project.salebe.dto.user.ResponseObject;
import com.project.salebe.service.AdminService;
import com.project.salebe.util.JsonUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/admin")
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    JsonUtil jsonUtil;

    @Autowired
    private AdminService adminService;

    @PostMapping("/search")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public SearchAdminResponse searchAdmin(@RequestBody SearchAdminRequest searchRequest) {
        log.info("User search admin: {}", jsonUtil.toJson(searchRequest));
        return adminService.searchAdmin(searchRequest);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminResponse getById(@PathVariable String id) {
        log.info("Xem chi tiet admin viu id : {}", id);
        return adminService.getAdminById(id);
    }

    //    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/register")
    public AdminResponse createAdmin(@Valid @ModelAttribute AdminRegisterRequest request) {
        log.info("Admin create user : {}", request.getPhoneNumber());
        return adminService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminResponse updateAdmin(@PathVariable String id, @Validated @ModelAttribute AndminUpdateRequest request) {
        log.info("chinh sua thong tin nhan vien voi id {} va thong tin {}", id, request.getPhoneNumber());
        return adminService.update(id, request);
    }

    @PutMapping("/reset-password/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> resetPassword(@PathVariable String id, @RequestBody ResetPassword resetPassword) {
        log.info("Thay doi mat khau : {}", id);
        adminService.resetPassword(id, resetPassword);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Reset password successfully")
                .data(resetPassword.getPassword())
                .status(HttpStatus.OK)
                .build());
    }

    @PutMapping("/block/{userId}/{active}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> blockOrEnable(
            @Valid @PathVariable String userId,
            @Valid @PathVariable int active
    ) throws Exception {
        adminService.blockOrEnable(userId, active > 0);
        String message = active > 0 ? "Successfully enabled the user." : "Successfully blocked the user.";
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(message)
                .status(HttpStatus.OK)
                .data(null)
                .build());
    }
}