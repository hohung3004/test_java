package com.project.salebe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.salebe.dto.admin.request.AdminRegisterRequest;
import com.project.salebe.dto.admin.request.AndminUpdateRequest;
import com.project.salebe.dto.admin.request.ResetPassword;
import com.project.salebe.dto.admin.request.SearchAdminRequest;
import com.project.salebe.dto.admin.response.AdminResponse;
import com.project.salebe.dto.admin.response.SearchAdminResponse;
import com.project.salebe.dto.schedules.response.ScheduleResponse;
import com.project.salebe.dto.user.ResponseObject;
import com.project.salebe.entity.RoleEntity;
import com.project.salebe.entity.Token;
import com.project.salebe.entity.UserEntity;
import com.project.salebe.enums.ErrorCode;
import com.project.salebe.exceptions.DataNotFoundException;
import com.project.salebe.exceptions.SaleappException;
import com.project.salebe.repository.CommonRepositoryCustom;
import com.project.salebe.repository.RoleRepository;
import com.project.salebe.repository.TokenRepository;
import com.project.salebe.repository.UserRepository;
import com.project.salebe.util.JsonUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminService {
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
    LocalImageService localImageService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ScheduleService scheduleService;

    public SearchAdminResponse searchAdmin(SearchAdminRequest request) {
        try {
            Page<UserEntity> userEntity = commonRepositoryCustom.searchAdmin(request);
            List<AdminResponse> listDTO = new ArrayList<>();
            userEntity.getContent().forEach(admin -> {
                AdminResponse adminResponse = modelMapper.map(admin, AdminResponse.class);
                List<String> profileImageUrls = new ArrayList<>();
                if (admin.getProfileImage() != null && !admin.getProfileImage().isEmpty()) {
                    try {
                        List<Map<String, String>> imageList = objectMapper.readValue(admin.getProfileImage(), new TypeReference<List<Map<String, String>>>() {
                        });
                        for (Map<String, String> imageMap : imageList) {
                            String imageUrl = imageMap.get("url");
                            profileImageUrls.add(imageUrl);
                        }
                    } catch (Exception e) {
                        log.error("Error parsing profile images: {}", e.getMessage(), e);
                    }
                }
                adminResponse.setProfileImage(profileImageUrls);
                listDTO.add(adminResponse);
            });
            SearchAdminResponse response = new SearchAdminResponse();
            response.setRecordSize(userEntity.getTotalElements());
            response.setList(listDTO);
            return response;
        } catch (Exception e) {
            log.error("Has some error when search {}:", e.getMessage(), e);
            throw new SaleappException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public AdminResponse getAdminById(String id) {
        try {
            Optional<UserEntity> userEntity = userRepository.findById(id);
            if (userEntity.isPresent()) {
                AdminResponse adminResponse = modelMapper.map(userEntity, AdminResponse.class);
                ScheduleResponse scheduleResponse = scheduleService.getScheduleById(adminResponse.getScheduleId());
                List<String> profileImageUrls = new ArrayList<>();
                if (userEntity.get().getProfileImage() != null && !userEntity.get().getProfileImage().isEmpty()) {
                    try {
                        List<Map<String, String>> imageList = objectMapper.readValue(userEntity.get().getProfileImage(), new TypeReference<List<Map<String, String>>>() {
                        });
                        for (Map<String, String> imageMap : imageList) {
                            String imageUrl = imageMap.get("url");
                            profileImageUrls.add(imageUrl);
                        }
                    } catch (Exception e) {
                        log.error("Error parsing profile images: {}", e.getMessage(), e);
                    }
                }
                adminResponse.setProfileImage(profileImageUrls);
                // Set the calculated time difference in adminResponse
                adminResponse.setTimeIn(scheduleResponse.getTimeIn());
                return adminResponse;
            } else {
                log.info("Id nhan vien khong ton tai : {}", id);
                return null;
            }
        } catch (Exception e) {
            log.error("Co loi xay ra voi id : {}", id);
            throw new SaleappException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public AdminResponse create(AdminRegisterRequest userRequest) {
        try {
            if (userRequest.getPassword().equals(userRequest.getRetypePassword())) {
                Optional<UserEntity> userPhoneEntity = userRepository.findByPhoneNumber(userRequest.getPhoneNumber());
                Optional<UserEntity> userEmailEntity = userRepository.findByEmail(userRequest.getEmail());
                if (!userRequest.getPhoneNumber().isBlank() && userPhoneEntity.isPresent()) {
                    log.error("So dien thoai da ton tai: {}", userRequest.getPhoneNumber());
                    throw new SaleappException(ErrorCode.PHONE_NUMBER_EXISTS);
                }
                if (!userRequest.getEmail().isBlank() && userEmailEntity.isPresent()) {
                    log.error("Email da ton tai: {}", userRequest.getPhoneNumber());
                    throw new SaleappException(ErrorCode.VALUE_EXISTS);
                }
                RoleEntity role = roleRepository.findById(userRequest.getRoleId())
                        .orElseThrow(() ->
                                new SaleappException(ErrorCode.RECORD_NOT_FOUND));
                // Tạo mã nhân viên ngẫu nhiên
                String employeeCode = generateRandomEmployeeCode(10); // 8 là độ dài mã nhân viên mong muốn

                UserEntity userEntity = modelMapper.map(userRequest, UserEntity.class);
                if (userRequest.getProfileImage() != null) {
                    List<Map<String, String>> images = localImageService.uploadFile(userRequest.getProfileImage());
                    String jsonProfileImage = new ObjectMapper().writeValueAsString(images);
                    userEntity.setProfileImage(jsonProfileImage);
                } else {
                    userEntity.setProfileImage(null);
                }

                String password = userRequest.getPassword();
                String encodedPassword = passwordEncoder.encode(password);
                userEntity.setPassword(encodedPassword);
                userEntity.setRole(role);
                userEntity.setCode(employeeCode); // Gán mã nhân viên ngẫu nhiên
                userEntity.setActive(true);
                return modelMapper.map(userRepository.save(userEntity), AdminResponse.class);
            } else {
                log.error("Mat khau khong trung khop !");
                throw new SaleappException(ErrorCode.CREATE_FAILED);
            }
        } catch (Exception e) {
            log.error("Tao tai khoan that bai {}", e.getMessage());
            throw new SaleappException(ErrorCode.CREATE_FAILED);
        }
    }

    @Transactional
    public AdminResponse update(String id, AndminUpdateRequest request) {
        try {
            Optional<UserEntity> userEntity = userRepository.findById(id);
            Optional<UserEntity> userPhoneEntity = userRepository.findByPhoneNumber(request.getPhoneNumber());
            Optional<UserEntity> userEmailEntity = userRepository.findByEmail(request.getEmail());

            if (userEntity.isEmpty()) {
                log.info("ID {} nhân viên/admin không tồn tại", id);
                throw new SaleappException(ErrorCode.RECORD_NOT_FOUND);
            } else {
                if (userPhoneEntity.isPresent() && !userPhoneEntity.get().getId().equals(userEntity.get().getId())) {
                    log.error("SĐT đã tồn tại: {}", request.getPhoneNumber());
                    throw new SaleappException(ErrorCode.PHONE_NUMBER_EXISTS);
                }
                if (userEmailEntity.isPresent() && !userEmailEntity.get().getId().equals(userEntity.get().getId())) {
                    log.error("Email đã tồn tại: {}", request.getEmail());
                    throw new SaleappException(ErrorCode.EMAIL_EXISTS);
                }
                String urlImages = userEntity.get().getProfileImage();
                if (userEntity.get().getProfileImage() != null && !userEntity.get().getProfileImage().equals("[]")) {
                    String profileImageString = userEntity.get().getProfileImage().toString();
                    // Chuyển đổi chuỗi JSON thành danh sách các map
                    List<Map<String, String>> images = objectMapper.readValue(profileImageString, new TypeReference<List<Map<String, String>>>() {
                    });
                    // Trích xuất danh sách các publicId
                    List<String> publicIdsToDelete = images.stream()
                            .map(image -> image.get("publicId"))
                            .collect(Collectors.toList());
                    // Gọi phương thức deleteFilesByPublicIds để xóa các ảnh dựa trên publicId
                    localImageService.deleteFilesByPublicIds(publicIdsToDelete);
                }
                RoleEntity role = roleRepository.findById(request.getRoleId())
                        .orElseThrow(() -> new SaleappException(ErrorCode.RECORD_NOT_FOUND));

                modelMapper.map(request, userEntity.get());
                userEntity.get().setRole(role);
                userEntity.get().setActive(true);
                userEntity.get().setCode(userEntity.get().getCode());
                userEntity.get().setPassword(userEntity.get().getPassword());

                if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
                    List<Map<String, String>> images = localImageService.uploadFile(request.getProfileImage());
                    String jsonProfileImage = new ObjectMapper().writeValueAsString(images);
                    userEntity.get().setProfileImage(jsonProfileImage);
                } else {
                    userEntity.get().setProfileImage(urlImages);
                }
                return modelMapper.map(userRepository.save(userEntity.get()), AdminResponse.class);
            }
        } catch (JsonProcessingException e) {
            log.error("Lỗi khi chuyển đổi danh sách hình ảnh thành chuỗi JSON: {}", e.getMessage());
            throw new SaleappException(ErrorCode.UPDATE_FAILED);
        } catch (Exception e) {
            log.error("Cập nhật thông tin thất bại: {}", e.getMessage());
            throw new SaleappException(ErrorCode.UPDATE_FAILED);
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
                throw new SaleappException(ErrorCode.UPDATE_FAILED);
            }
        } else {
            log.error("Id không tồn tại !");
            throw new SaleappException(ErrorCode.RECORD_NOT_FOUND);
        }
        return null;
    }

    @Transactional
    public void blockOrEnable(String userId, Boolean active) throws DataNotFoundException {
        UserEntity existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        existingUser.setActive(active);
        userRepository.save(existingUser);
    }

    // Hàm tạo mã nhân viên ngẫu nhiên
    private String generateRandomEmployeeCode(int length) {
        // Định dạng LocalDateTime thành chuỗi không chứa khoảng trắng hoặc dấu hai chấm
        String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789" + formattedDateTime;
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }
        return code.toString();
    }

}
