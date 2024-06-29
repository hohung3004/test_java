package com.project.javatestfresher.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.project.javatestfresher.dto.auth.ResponseObject;
import com.project.javatestfresher.dto.user.request.*;
import com.project.javatestfresher.dto.user.response.AdminResponse;
import com.project.javatestfresher.dto.user.response.ImportToolResponse;
import com.project.javatestfresher.dto.user.response.SearchUserResponse;
import com.project.javatestfresher.dto.user.response.UserError;
import com.project.javatestfresher.entity.RoleEntity;
import com.project.javatestfresher.entity.Token;
import com.project.javatestfresher.entity.UserEntity;
import com.project.javatestfresher.enums.ErrorCode;
import com.project.javatestfresher.exceptions.ManagerException;
import com.project.javatestfresher.repository.CommonRepositoryCustom;
import com.project.javatestfresher.repository.RoleRepository;
import com.project.javatestfresher.repository.TokenRepository;
import com.project.javatestfresher.repository.UserRepository;
import com.project.javatestfresher.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@Service
public class UserService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CommonRepositoryCustom commonRepositoryCustom;
    @Autowired
    private JsonUtil jsonUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    ObjectMapper objectMapper;


    public SearchUserResponse searchAdmin(SearchAdminRequest request) {
        try {
            Page<UserEntity> userEntity = commonRepositoryCustom.searchAdmin(request);
            List<AdminResponse> listDTO = new ArrayList<>();
            userEntity.getContent().forEach(admin -> {
                AdminResponse adminResponse = modelMapper.map(admin, AdminResponse.class);
                adminResponse.setUserName(admin.getUsername());
                listDTO.add(adminResponse);
            });
            SearchUserResponse response = new SearchUserResponse();
            response.setRecordSize(userEntity.getTotalElements());
            response.setList(listDTO);
            return response;
        } catch (Exception e) {
            log.error("Has some error when search {}:", e.getMessage(), e);
            throw new ManagerException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public AdminResponse getAdminById(String id) {
        try {
            Optional<UserEntity> userEntity = userRepository.findById(id);
            if (userEntity.isPresent()) {
                AdminResponse adminResponse = modelMapper.map(userEntity, AdminResponse.class);

                return adminResponse;
            } else {
                log.info("Id nhan vien khong ton tai : {}", id);
                return null;
            }
        } catch (Exception e) {
            log.error("Co loi xay ra voi id : {}", id);
            throw new ManagerException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public AdminResponse create(AdminRequest userRequest) {
        try {
            if (userRequest.getPassword().equals(userRequest.getRetypePassword())) {
                Optional<UserEntity> userPhoneEntity = userRepository.findByUserName(userRequest.getUserName());

                if (!userRequest.getUserName().isBlank() && userPhoneEntity.isPresent()) {
                    log.error("username da ton tai: {}", userRequest.getRetypePassword());
                    throw new ManagerException(ErrorCode.PHONE_NUMBER_EXISTS);
                }

                RoleEntity role = roleRepository.findById(userRequest.getRoleId())
                        .orElseThrow(() ->
                                new ManagerException(ErrorCode.RECORD_NOT_FOUND));

                UserEntity userEntity = modelMapper.map(userRequest, UserEntity.class);
                userEntity.setUserName(userRequest.getUserName());
                String password = userRequest.getPassword();
                String encodedPassword = passwordEncoder.encode(password);
                userEntity.setPassword(encodedPassword);
                userEntity.setRole(role);

                userEntity.setValid(true);
                return modelMapper.map(userRepository.save(userEntity), AdminResponse.class);
            } else {
                log.error("Mat khau khong trung khop !");
                throw new ManagerException(ErrorCode.CREATE_FAILED);
            }
        } catch (Exception e) {
            log.error("Tao tai khoan that bai {}", e.getMessage());
            throw new ManagerException(ErrorCode.CREATE_FAILED);
        }
    }

    @Transactional
    public AdminResponse update(String id, AndminUpdateRequest request) {
        try {
            Optional<UserEntity> userEntity = userRepository.findById(id);
            Optional<UserEntity> userPhoneEntity = userRepository.findByUserName(request.getUserName());

            if (userEntity.isEmpty()) {
                log.info("ID {} nhân viên/admin không tồn tại", id);
                throw new ManagerException(ErrorCode.RECORD_NOT_FOUND);
            } else {
                if (userPhoneEntity.isPresent() && !userPhoneEntity.get().getId().equals(userEntity.get().getId())) {
                    log.error("SĐT đã tồn tại: {}", request.getUserName());
                    throw new ManagerException(ErrorCode.PHONE_NUMBER_EXISTS);
                }


                RoleEntity role = roleRepository.findById(request.getRoleId())
                        .orElseThrow(() -> new ManagerException(ErrorCode.RECORD_NOT_FOUND));

                modelMapper.map(request, userEntity.get());
                userEntity.get().setRole(role);
                userEntity.get().setValid(true);
                userEntity.get().setPassword(userEntity.get().getPassword());

                return modelMapper.map(userRepository.save(userEntity.get()), AdminResponse.class);
            }
        } catch (Exception e) {
            log.error("Cập nhật thông tin thất bại: {}", e.getMessage());
            throw new ManagerException(ErrorCode.UPDATE_FAILED);
        }
    }

    @Transactional
    public ResponseEntity<ResponseObject> resetPassword(String userId, ResetPassword newPassword) {
        Optional<UserEntity> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            if (newPassword.getPassword().equals(newPassword.getRetypePassword())) {
                String password = newPassword.getPassword();
                String encodedPassword = passwordEncoder.encode(password);
                existingUser.get().setPassword(encodedPassword);
                userRepository.save(existingUser.get());
                //reset password => clear token
                List<Token> tokens = tokenRepository.findByUser(existingUser.get());
                for (Token token : tokens) {
                    tokenRepository.delete(token);
                }
            } else {
                log.error("Mat khau khong trung khop !");
                throw new ManagerException(ErrorCode.UPDATE_FAILED);
            }
        } else {
            log.error("Id không tồn tại !");
            throw new ManagerException(ErrorCode.RECORD_NOT_FOUND);
        }
        return null;
    }

    public ImportToolResponse importTools(ImportUserRequest request) {
        ImportToolResponse importResponse = new ImportToolResponse();
        List<UserError> listToolErrors = new ArrayList<>();
        int countError = 0;
        int countSuccess = 0;
        for (AdminRequest userRequest : request.getListTool()) {
            UserError toolError = modelMapper.map(userRequest, UserError.class);
            try {
                if (userRepository.findByUserName(userRequest.getUserName()).isPresent()) {
                    modelMapper.map(userRequest, toolError);
                    toolError.setMessage(userRequest.getUserName() + " already exits");
                    listToolErrors.add(toolError);
                    countError++;
                } else {
                    UserEntity userEntity = modelMapper.map(userRequest, UserEntity.class);
                    userRepository.save(userEntity);
                    countSuccess++;
                }
            } catch (Exception ex) {
                countError++;
                toolError.setMessage("failed to save user because " + ex.getMessage());
                listToolErrors.add(toolError);
            }
        }
        importResponse.setMessage(countSuccess + " records successfully, " + countError + " failed");
        importResponse.setListError(listToolErrors);
        return importResponse;
    }
}
