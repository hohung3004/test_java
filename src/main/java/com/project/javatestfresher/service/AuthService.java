package com.project.salebe.service;

import com.project.salebe.components.JwtTokenUtils;
import com.project.salebe.components.LocalizationUtils;
import com.project.salebe.dto.auth.request.LoginRequest;
import com.project.salebe.entity.Token;
import com.project.salebe.entity.UserEntity;
import com.project.salebe.enums.ErrorCode;
import com.project.salebe.exceptions.ExpiredTokenException;
import com.project.salebe.exceptions.SaleappException;
import com.project.salebe.repository.RoleRepository;
import com.project.salebe.repository.TokenRepository;
import com.project.salebe.repository.UserRepository;
import com.project.salebe.util.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtils jwtTokenUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    public String login(LoginRequest userLoginDTO) throws Exception {
        Optional<UserEntity> optionalUser = Optional.empty();
        String subject = null;
        // Check if the user exists by phone number
        if (userLoginDTO.getPhoneNumber() != null && !userLoginDTO.getPhoneNumber().isBlank()) {
            optionalUser = userRepository.findByPhoneNumber(userLoginDTO.getPhoneNumber());
            subject = userLoginDTO.getPhoneNumber();
        }

        // If the user is not found by phone number, check by email
        if (optionalUser.isEmpty() && userLoginDTO.getEmail() != null) {
            optionalUser = userRepository.findByEmail(userLoginDTO.getEmail());
            subject = userLoginDTO.getEmail();
        }

        // If user is not found, throw an exception
        if (optionalUser.isEmpty()) {
            throw new SaleappException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // Get the existing user
        UserEntity existingUser = optionalUser.get();

        //check password
        if (existingUser.getFacebookAccountId() == 0
                && existingUser.getGoogleAccountId() == 0) {
            if (!passwordEncoder.matches(userLoginDTO.getPassword(), existingUser.getPassword())) {
                throw new SaleappException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        /*
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if(optionalRole.isEmpty() || !roleId.equals(existingUser.getRole().getId())) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS));
        }
        */
        if (!existingUser.isActive()) {
            throw new SaleappException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                subject, userLoginDTO.getPassword(),
                existingUser.getAuthorities()
        );

        //authenticate with Java Spring security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }
    public UserEntity getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtil.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }
        String subject = jwtTokenUtil.getSubject(token);
        Optional<UserEntity> user;
        user = userRepository.findByPhoneNumber(subject);
        if (user.isEmpty() && ValidationUtils.isValidEmail(subject)) {
            user = userRepository.findByEmail(subject);
        }
        return user.orElseThrow(() -> new Exception("User not found"));
    }
    public UserEntity getUserDetailsFromRefreshToken(String refreshToken) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        return getUserDetailsFromToken(existingToken.getToken());
    }

}
