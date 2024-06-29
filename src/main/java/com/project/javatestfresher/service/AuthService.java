package com.project.javatestfresher.service;


import com.project.javatestfresher.components.JwtTokenUtils;
import com.project.javatestfresher.dto.auth.request.LoginRequest;
import com.project.javatestfresher.entity.Token;
import com.project.javatestfresher.entity.UserEntity;
import com.project.javatestfresher.enums.ErrorCode;
import com.project.javatestfresher.exceptions.ExpiredTokenException;
import com.project.javatestfresher.exceptions.ManagerException;
import com.project.javatestfresher.repository.RoleRepository;
import com.project.javatestfresher.repository.TokenRepository;
import com.project.javatestfresher.repository.UserRepository;
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
        if (userLoginDTO.getUserName() != null && !userLoginDTO.getUserName().isBlank()) {
            optionalUser = userRepository.findByUserName(userLoginDTO.getUserName());
            subject = userLoginDTO.getUserName();
        }


        // If user is not found, throw an exception
        if (optionalUser.isEmpty()) {
            throw new ManagerException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // Get the existing user
        UserEntity existingUser = optionalUser.get();

        //check password

            if (!passwordEncoder.matches(userLoginDTO.getPassword(), existingUser.getPassword())) {
                throw new ManagerException(ErrorCode.INTERNAL_SERVER_ERROR);
            }

        /*
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if(optionalRole.isEmpty() || !roleId.equals(existingUser.getRole().getId())) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS));
        }
        */
        if (!existingUser.isValid()) {
            throw new ManagerException(ErrorCode.INTERNAL_SERVER_ERROR);
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
        user = userRepository.findByUserName(subject);

        return user.orElseThrow(() -> new Exception("User not found"));
    }
}
