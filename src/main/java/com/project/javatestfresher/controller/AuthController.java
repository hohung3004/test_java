package com.project.javatestfresher.controller;

import com.project.javatestfresher.dto.auth.ResponseObject;
import com.project.javatestfresher.dto.auth.request.LoginRequest;
import com.project.javatestfresher.dto.auth.respose.LoginResponse;
import com.project.javatestfresher.entity.Token;
import com.project.javatestfresher.entity.UserEntity;
import com.project.javatestfresher.enums.ErrorCode;
import com.project.javatestfresher.service.AuthService;
import com.project.javatestfresher.service.TokenService;
import com.project.javatestfresher.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    JsonUtil jsonUtil;

    @Autowired
    private AuthService authService;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(
            @Valid @RequestBody LoginRequest userLoginDTO,
            HttpServletRequest request
    ) throws Exception {
        // Kiểm tra thông tin đăng nhập và sinh token
        String token = authService.login(userLoginDTO);

        UserEntity userDetail = authService.getUserDetailsFromToken(token);
        Token jwtToken = tokenService.addToken(userDetail, token);

        LoginResponse loginResponse = LoginResponse.builder()
                .message(String.valueOf(ErrorCode.SUCCESS))
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                .id(userDetail.getId())
                .build();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Login successfully")
                .data(loginResponse)
                .status(HttpStatus.OK)
                .build());
    }
}